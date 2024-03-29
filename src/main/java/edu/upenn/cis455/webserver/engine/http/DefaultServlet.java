package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.engine.http.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(DefaultServlet.class);

    private final static DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private ServletContext context;
    private String rootDirectory;
    private Map<String, String> initParams = new HashMap<>();
    private String name;
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {

        Enumeration paramNames = config.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

        this.name = config.getServletName();
        this.context = config.getServletContext();
        this.config = config;

        this.rootDirectory = context.getRealPath("/");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, List<String>> map = req.getParameterMap();

        PrintWriter writer = resp.getWriter();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {

            writer.print(entry.getKey() + ": ");

            for (String val : entry.getValue()) {

                writer.print(val + ", ");

            }

        }

    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File fileRequested = new File(rootDirectory + request.getRequestURI());

        if (fileRequested.canRead() && fileRequested.isDirectory()) {

            log.info(String.format("DefaultServlet Serving HEAD Request for Directory %s",
                    fileRequested.getName()));

            response.setStatus(200);
            response.setContentType("text/html");

            log.info(String.format("Directory Listing of %s Sent to Client", fileRequested
                    .getName()));

        } else if (fileRequested.canRead()) {

            log.info(String.format("DefaultServlet Serving HEAD Request for %s", fileRequested
                    .getName()));

            response.setStatus(200);
            response.addHeader("Last-Modified", FileUtil.getLastModifiedGmt(fileRequested).format(HTTP_DATE_FORMAT));

            response.setContentType(getServletContext().getMimeType(fileRequested.getPath()));
            int contentLength = Long.valueOf(fileRequested.length()).intValue();
            response.setContentLength(contentLength);

            log.info(String.format("%s Sent to Client", fileRequested.getName()));

        } else {

            log.info(String.format("%s Not found", fileRequested.getName()));
            response.sendError(404);

            log.info("Not Found Error Sent to Client: " + request.getRequestURI());
        }

    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        File fileRequested = new File(rootDirectory + request.getRequestURI());

        if (request.getHeader("if-modified-since") != null) {

            handleIfModifiedSince(fileRequested, request.getHeader("if-modified-since"), response);

        } else if (request.getHeader("if-unmodified-since") != null) {

            handleIfUnmodifiedSince(fileRequested, request.getHeader("if-unmodified-since"), response);

        } else if (fileRequested.canRead() && fileRequested.isDirectory()) {

            response.setStatus(200);
            handleDirectory(fileRequested, response);

        } else if (fileRequested.canRead()) {

            response.setStatus(200);
            response.addHeader("Last-Modified", FileUtil.getLastModifiedGmt(fileRequested).format(HTTP_DATE_FORMAT));
            handleFile(fileRequested, response);

        } else if (!fileRequested.exists()) {

            response.sendError(404);

        } else {
            response.sendError(401, "Unauthorized");
        }

    }

    private void handleDirectory(File file, HttpServletResponse response) throws IOException {

        if (file == null) {
            log.error("Passed null File");
            return;
        }

        log.info(String.format("DefaultServlet Serving GET Request for Directory %s", file.getName()));
        response.setContentType("text/html");

        PrintWriter writer = response.getWriter();

        File[] directoryListing = file.listFiles();
        if (directoryListing == null) {
            throw new IOException();
        }

        writer.print("<html><body>");
        for (File f : directoryListing) {
            String relativePath = FileUtil.relativizePath(f.getAbsolutePath(), rootDirectory);
            writer.print(String.format("<p><a href=\"%s\">%s</a></p>", relativePath, f.getName()));
        }
        writer.print("</html></body>");

        log.info(String.format("Directory Listing of %s Sent to Client", file.getName()));
    }

    private void handleFile(File file, HttpServletResponse response) throws IOException {

        log.info(String.format("DefaultServlet Serving GET Request for %s", file.getName()));


        response.setContentType(getServletContext().getMimeType(file.getPath()));
        response.getOutputStream();

        /* Send file as binary to output stream */
        FileUtil.copy(file, response.getOutputStream());

        log.info(String.format("%s Sent to Client", file.getName()));
    }


    @Override
    public void destroy() {



    }


    public void handleIfUnmodifiedSince(File file, String ifUnmodifiedDateString, HttpServletResponse response) throws
            ServletException {

        ZonedDateTime ifUnmodifiedSinceDate;
        try {
            ifUnmodifiedSinceDate = ZonedDateTime.parse(ifUnmodifiedDateString, HTTP_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ServletException(new BadRequestException());
        }

        ZonedDateTime lastModifiedDate = FileUtil.getLastModifiedGmt(file);

        if (ifUnmodifiedSinceDate.isAfter(lastModifiedDate)) {

            response.setStatus(200);
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

            try {
                handleFile(file, response);
            } catch (IOException e) {
                throw new ServletException(e);
            }

        } else {

            response.setStatus(412);
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

        }
    }

    public void handleIfModifiedSince(File file, String ifModifiedDateString, HttpServletResponse response) throws
            ServletException, IOException {

        ZonedDateTime ifModifiedSinceDate;
        try {
            ifModifiedSinceDate = ZonedDateTime.parse(ifModifiedDateString, HTTP_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ServletException(e);
        }
        ZonedDateTime lastModifiedDate = FileUtil.getLastModifiedGmt(file);

        if (lastModifiedDate.isAfter(ifModifiedSinceDate)) {

            response.setStatus(200);
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));

            handleFile(file, response);


        } else {

            response.setStatus(304);
            response.addHeader("Last-Modified", lastModifiedDate.format(HTTP_DATE_FORMAT));
        }

    }


    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getServletName() {
        return name;
    }


}
