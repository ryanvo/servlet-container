package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.exception.http.BadRequestException;
import edu.upenn.cis.cis455.webserver.exception.http.UnsupportedRequestException;
import edu.upenn.cis.cis455.webserver.servlet.io.ChunkedWriter;
import edu.upenn.cis.cis455.webserver.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.Map;

public class DefaultServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(DefaultServlet.class);

    private final static String HTTP_VERSION = "HTTP/1.1";
    private final static DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private final String rootDirectory;
    private Map<String, String> initParams;

    /**
     * @param rootDirectory path to the www folder
     */
    public DefaultServlet(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void init(ServletConfig config)  throws ServletException {

        Enumeration paramNames = config.getInitParameterNames();
        while(paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws ServletException {

        if (request.containsHeader("if-modified-since")) {
            String ifModified = request.getHeader("if-modified-since");
            log.error(ifModified);
            File file = new File(rootDirectory + request.getRequestURI());

            ZonedDateTime ifModifiedSinceDate;
            try {
                ifModifiedSinceDate = ZonedDateTime.parse(ifModified, HTTP_DATE_FORMAT);
            } catch (DateTimeParseException e) {
                throw new ServletException(e);
            }
            ZonedDateTime lastModifiedDate = getLastModifiedDate(file);

            if (lastModifiedDate.isAfter(ifModifiedSinceDate)) {

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

                try {
                    handleFile(file, response);
                } catch (IOException e) {
                    throw new ServletException(new BadRequestException());
                }

            } else {

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("304");
                response.setErrorMessage("Not Modified");
                response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));
                PrintWriter writer = response.getWriter();
                writer.println(response.getStatusAndHeader());
                writer.flush();
            }
            return;
        }

        if (request.containsHeader("if-unmodified-since")) {
            String ifUnmodified = request.getHeader("if-unmodified-since");

            File file = new File(rootDirectory + request.getRequestURI());
            ZonedDateTime ifUnmodifiedSinceDate;
            try {
                ifUnmodifiedSinceDate = ZonedDateTime.parse(ifUnmodified, HTTP_DATE_FORMAT);
            } catch (DateTimeParseException e) {
                throw new ServletException(new BadRequestException());
            }

            ZonedDateTime lastModifiedDate = getLastModifiedDate(file);

            if (ifUnmodifiedSinceDate.isAfter(lastModifiedDate)) {
                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

                try {
                    handleFile(file, response);
                } catch (IOException e) {
                    throw new ServletException(e);
                }
            } else {

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("412");
                response.setErrorMessage("Precondition Failed");
                response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));
                PrintWriter writer = response.getWriter();
                writer.println(response.getStatusAndHeader());
                writer.flush();
            }
            return;
        }


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

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.setContentType("text/html");

                writer.println(response.getStatusAndHeader());
                writer.flush();

                log.info(String.format("Directory Listing of %s Sent to Client", fileRequested
                        .getName()));

            } else if (fileRequested.canRead()) {

                log.info(String.format("DefaultServlet Serving HEAD Request for %s", fileRequested
                        .getName()));

                response.addHeader("Last-Modified", getLastModifiedDate(fileRequested).format(HTTP_DATE_FORMAT));

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.setContentType(FileUtil.probeContentType(fileRequested.getPath()));
                int contentLength = Long.valueOf(fileRequested.length()).intValue();
                response.setContentLength(contentLength);
                writer.println(response.getStatusAndHeader());
                writer.flush();

                log.info(String.format("%s Sent to Client", fileRequested.getName()));

            } else {

                log.info(String.format("%s Not found", fileRequested.getName()));


                response.setVersion(HTTP_VERSION);
                response.setStatusCode("404");
                response.setErrorMessage("Not Found");

                writer.println(response.getStatusAndHeader());
                writer.flush();

                log.info("Not Found Error Sent to Client" + request.getRequestURI());
            }

            log.debug(response.getStatusAndHeader());

        }
    }

    public void doGet(HttpRequest request, HttpResponse response) throws ServletException {

        File fileRequested = new File(rootDirectory + request.getRequestURI());
        try {
            if (!fileRequested.exists()) {

                handleFileNotFound(fileRequested, response);

            } else if (fileRequested.canRead() && fileRequested.isDirectory()) {
                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");

                handleDirectory(fileRequested, response);

            } else if (fileRequested.canRead()) {
                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");

                response.addHeader("Last-Modified", getLastModifiedDate(fileRequested).format(HTTP_DATE_FORMAT));
                handleFile(fileRequested, response);

            } else {

                response.sendError(401, "Unauthorized");

            }
        } catch (IOException e) {

            response.sendError(500, "Server IO Error");

            log.debug(response.getStatusAndHeader());

        }

    }

    private void handleDirectory(File file, HttpResponse response) throws IOException {

        log.info(String.format("DefaultServlet Serving GET Request for Directory %s",
                file.getName()));


        response.setContentType("text/html");
        response.addHeader("Transfer-Encoding", "chunked");

        ChunkedWriter writer = new ChunkedWriter(response.getOutputStream());
        writer.unchunkedPrintLn(response.getStatusAndHeader());

        writer.write("<html><body>");
        for (File f : file.listFiles()) {
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
//        InputStream fileInputStream = new FileInputStream(file);
//        byte[] buf = new byte[contentLength];
//        int bytesRead;
//        while ((bytesRead = fileInputStream.read(buf, 0, buf.length)) > 0) {
//            response.getOutputStream().write(buf, 0, bytesRead);
//        }
//        response.getOutputStream().flush();
        copyFile(file, response.getOutputStream());

        log.info(String.format("%s Sent to Client", file.getName()));
    }

    private void handleFileNotFound(File file, HttpResponse response) {

        String NOT_FOUND_MESSAGE = "<html><body><h1>404 File Not Found</h1></body></html>";

        log.info(String.format("%s Not found", file.getName()));

        response.setVersion(HTTP_VERSION);
        response.setStatusCode("404");
        response.setErrorMessage("Not Found");
        response.setContentType("text/html");
        response.setContentLength(NOT_FOUND_MESSAGE.length());

        response.getWriter().println(response.getStatusAndHeader());
        response.getWriter().println(NOT_FOUND_MESSAGE);
        response.getWriter().flush();

        log.info("Not Found Error Sent to Client" + file.getName());
    }



    public ZonedDateTime getLastModifiedDate(File file) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("GMT"));
    }

    public void copyFile(File file, OutputStream out) throws IOException {
        int len = Long.valueOf(file.length()).intValue();
        log.error("going to copy " + file.getName() + " with size " + len);

        InputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[len];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buf, 0, buf.length)) > 0) {
            out.write(buf, 0, bytesRead);
        }
        out.flush();
        log.error("file copied");
    }

    @Override
    public void destroy() {}

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


}
