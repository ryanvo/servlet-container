package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.exception.http.BadRequestException;
import edu.upenn.cis.cis455.webserver.exception.http.UnsupportedRequestException;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.servlet.io.ChunkedWriter;
import edu.upenn.cis.cis455.webserver.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DefaultServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(DefaultServlet.class);

    private final static String HTTP_VERSION = "HTTP/1.1";
    private final static DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private final String rootDirectory;
    private Map<String, String> initParams = new HashMap<>();

    /**
     * @param rootDirectory path to the www folder
     */
    public DefaultServlet(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void init(ServletConfig config) throws ServletException {

        Enumeration paramNames = config.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws ServletException {
        switch (request.getMethod()) {
            case "GET":
                doGet(request, response);
                break;
            case "HEAD":
                doHead(request, response);
                break;
            default:
                throw new ServletException(new UnsupportedRequestException());
        }

    }


    @Override
    public void doHead(HttpRequest request, HttpResponse response) throws ServletException {

        File fileRequested = new File(rootDirectory + request.getRequestURI());

        try (PrintWriter writer = new PrintWriter(response.getOutputStream())) {

            if (fileRequested.canRead() && fileRequested.isDirectory()) {

                log.info(String.format("DefaultServlet Serving HEAD Request for Directory %s",
                        fileRequested.getName()));

                response.setStatus(200, "OK");
                response.setContentType("text/html");

                writer.println(response.getStatusAndHeader());
                writer.flush();

                log.info(String.format("Directory Listing of %s Sent to Client", fileRequested
                        .getName()));

            } else if (fileRequested.canRead()) {

                log.info(String.format("DefaultServlet Serving HEAD Request for %s", fileRequested
                        .getName()));

                response.addHeader("Last-Modified", FileUtil.getLastModifiedGmt(fileRequested).format(HTTP_DATE_FORMAT));
                response.setStatus(200, "OK");

                response.setContentType(FileUtil.probeContentType(fileRequested.getPath()));
                int contentLength = Long.valueOf(fileRequested.length()).intValue();
                response.setContentLength(contentLength);
                writer.println(response.getStatusAndHeader());
                writer.flush();

                log.info(String.format("%s Sent to Client", fileRequested.getName()));

            } else {

                log.info(String.format("%s Not found", fileRequested.getName()));

                response.setStatus(404, "Not Found");

                writer.println(response.getStatusAndHeader());
                writer.flush();

                log.info("Not Found Error Sent to Client: " + request.getRequestURI());
            }

            log.debug(response.getStatusAndHeader());

        }
    }

    public void doGet(HttpRequest request, HttpResponse response) throws ServletException {

        File fileRequested = new File(rootDirectory + request.getRequestURI());

        try {

            if (request.containsHeader("if-modified-since")) {

                handleIfModifiedSince(fileRequested, request.getHeader("if-modified-since"), response);

            } else if (request.containsHeader("if-unmodified-since")) {

                handleIfUnmodifiedSince(fileRequested, request.getHeader("if-unmodified-since"), response);

            } else if (!fileRequested.exists()) {

                handleFileNotFound(fileRequested, response);

            } else if (fileRequested.canRead() && fileRequested.isDirectory()) {

                response.setStatus(200, "OK");
                handleDirectory(fileRequested, response);

            } else if (fileRequested.canRead()) {

                response.setStatus(200, "OK");
                response.addHeader("Last-Modified", FileUtil.getLastModifiedGmt(fileRequested).format(HTTP_DATE_FORMAT));
                handleFile(fileRequested, response);

            } else {

                response.sendError(401, "Unauthorized");

            }

        } catch (IOException e) {
            log.error(response.getStatusAndHeader(), e);
            throw new ServletException(e);
        }

    }

    private void handleDirectory(File file, HttpResponse response) throws IOException {

        if (file == null) {
            return;
        }

        log.info(String.format("DefaultServlet Serving GET Request for Directory %s",
                file.getName()));


        response.setContentType("text/html");
        response.addHeader("Transfer-Encoding", "chunked");

        ChunkedWriter writer = new ChunkedWriter(response.getOutputStream());
        writer.unchunkedPrintLn(response.getStatusAndHeader());

        File[] directoryListing = file.listFiles();
        if (directoryListing == null) {
            throw new IOException();
        }

        writer.write("<html><body>");
        for (File f : directoryListing) {
            String relativePath = FileUtil.relativizePath(f.getAbsolutePath(), rootDirectory);
            writer.write(String.format("<p><a href=\"%s\">%s</a></p>", relativePath, f.getName()));
        }
        writer.write("</html></body>");
        writer.finish();

        log.info(String.format("Directory Listing of %s Sent to Client", file.getName()));

    }

    private void handleFile(File file, HttpResponse response) throws IOException {

        log.info(String.format("DefaultServlet Serving GET Request for %s", file.getName()));


        response.setContentType(FileUtil.probeContentType(file.getPath()));
        int contentLength = Long.valueOf(file.length()).intValue();
        response.setContentLength(contentLength);

        response.getOutputStream().write(response.getStatusAndHeader().getBytes());
        response.getOutputStream().write("\n".getBytes());

        /* Send file as binary to output stream */
        FileUtil.copy(file, response.getOutputStream());

        log.info(String.format("%s Sent to Client", file.getName()));
    }

    private void handleFileNotFound(File file, HttpResponse response) {

        String NOT_FOUND_MESSAGE = "<html><body><h1>404 File Not Found</h1></body></html>";

        log.info(String.format("%s Not found", file.getName()));

        response.setStatus(404, "Not Found");
        response.setContentType("text/html");
        response.setContentLength(NOT_FOUND_MESSAGE.length());

        response.getWriter().println(response.getStatusAndHeader());
        response.getWriter().println(NOT_FOUND_MESSAGE);
        response.getWriter().flush();

        log.info("Not Found Error Sent to Client" + file.getName());
    }


    @Override
    public void destroy() {
    }

    @Override
    public void doPost(HttpRequest req, HttpResponse resp) {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getServletName() {
        return null;
    }

    public void handleIfUnmodifiedSince(File file, String ifUnmodifiedDateString, HttpResponse response) throws ServletException {

        ZonedDateTime ifUnmodifiedSinceDate;
        try {
            ifUnmodifiedSinceDate = ZonedDateTime.parse(ifUnmodifiedDateString, HTTP_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ServletException(new BadRequestException());
        }

        ZonedDateTime lastModifiedDate = FileUtil.getLastModifiedGmt(file);

        if (ifUnmodifiedSinceDate.isAfter(lastModifiedDate)) {

            response.setStatus(200, "OK");
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

            try {
                handleFile(file, response);
            } catch (IOException e) {
                throw new ServletException(e);
            }
        } else {

            response.setStatus(412, "Precondition Failed");
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));
            PrintWriter writer = response.getWriter();
            writer.println(response.getStatusAndHeader());
            writer.flush();
        }
    }

    public void handleIfModifiedSince(File file, String ifModifiedDateString, HttpResponse response) throws
            ServletException {

        ZonedDateTime ifModifiedSinceDate;
        try {
            ifModifiedSinceDate = ZonedDateTime.parse(ifModifiedDateString, HTTP_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ServletException(e);
        }
        ZonedDateTime lastModifiedDate = FileUtil.getLastModifiedGmt(file);

        if (lastModifiedDate.isAfter(ifModifiedSinceDate)) {

            response.setStatus(200, "OK");
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

            try {
                handleFile(file, response);
            } catch (IOException e) {
                throw new ServletException(new BadRequestException());
            }

        } else {

            response.setStatus(304, "Not Modified");
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));
            PrintWriter writer = response.getWriter();
            writer.println(response.getStatusAndHeader());
            writer.flush();
        }
    }
}
