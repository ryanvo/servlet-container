package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.servlet.io.ChunkedWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Map;

public class DefaultServlet implements HttpServlet {

    private static Logger log = LogManager.getLogger(DefaultServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";
    private final String rootDirectory;
    private Map<String, String> initParams;
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    /**
     * @param rootDirectory path to the www folder
     */
    public DefaultServlet(String rootDirectory) {
        this.rootDirectory = rootDirectory;


    }

    @Override
    public void init(ServletConfig config) {

        Enumeration paramNames = config.getInitParameterNames();
        while(paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

    }

    @Override
    public void destroy() {



    }


    public void doGet(HttpRequest request, HttpResponse response) {

        File fileRequested = new File(rootDirectory + request.getRequestURI());
        try {
            if (!fileRequested.exists()) {
                handleFileNotFound(fileRequested, response);
            } else if (fileRequested.canRead() && fileRequested.isDirectory()) {

                handleDirectory(fileRequested, response);

            } else if (fileRequested.canRead()) {
                ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(fileRequested.lastModified()), ZoneId.of("GMT"));
                String lastModifiedTime = time.format(dateFormat);
                response.addHeader("Last-Modified", lastModifiedTime);

                handleFile(fileRequested, response);
            } else {
                response.sendError(401, "Unauthorized");
            }
        } catch (IOException e) {

            response.sendError(500, "Server IO Error");

            log.debug(response.getStatusAndHeader());
            //TODO error

        }

    }

    private void handleDirectory(File file, HttpResponse response) throws IOException {

        log.info(String.format("DefaultServlet Serving GET Request for Directory %s",
                file.getName()));

        response.setVersion(HTTP_VERSION);
        response.setStatusCode("200");
        response.setErrorMessage("OK");
        response.setContentType("text/html");
        response.addHeader("Transfer-Encoding", "chunked");

        ChunkedWriter writer = new ChunkedWriter(response.getOutputStream());

        writer.unchunkedPrintLn(response.getStatusAndHeader());

        writer.write("<html><body>");
        for (File f : file.listFiles()) {
            Path rootPath = Paths.get(rootDirectory);
            Path fileAbsolutePath = Paths.get(f.getAbsolutePath());
            Path relativePath = rootPath.relativize(fileAbsolutePath);
            writer.write(String.format("<p><a href=\"%s\">%s</a></p>",
                    relativePath.toString(), f.getName()));
        }
        writer.write("</html></body>");
        writer.finish();

        log.info(String.format("Directory Listing of %s Sent to Client", file.getName()));

    }

    private void handleFile(File file, HttpResponse response) throws IOException {

        log.info(String.format("DefaultServlet Serving GET Request for %s", file.getName()));

        response.setVersion(HTTP_VERSION);
        response.setStatusCode("200");
        response.setErrorMessage("OK");
        response.setContentType(probeContentType(file.getPath()));
        int contentLength = Long.valueOf(file.length()).intValue();
        response.setContentLength(contentLength);

        response.getOutputStream().write(response.getStatusAndHeader().getBytes());
        response.getOutputStream().write("\n".getBytes());


        /* Send file as binary to output stream */
        InputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[contentLength];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buf, 0, buf.length)) > 0) {
            response.getOutputStream().write(buf, 0, bytesRead);
        }
        response.getOutputStream().flush();

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

    public void doHead(HttpRequest request, HttpResponse response) {

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

                ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(fileRequested.lastModified()), ZoneId.of("GMT"));
                String lastModifiedTime = time.format(dateFormat);
                response.addHeader("Last-Modified", lastModifiedTime);

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.setContentType(probeContentType(fileRequested.getPath()));
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


    public String probeContentType(String filePath) {

        filePath = filePath.toLowerCase();

        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {

            return "image/jpeg";

        } else if (filePath.endsWith(".gif")) {

            return "image/gif";

        } else if (filePath.endsWith(".png")) {

            return "image/png";

        } else if (filePath.endsWith(".txt")) {

            return "text/plain";

        } else if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {

            return "text/html";

        } else {

            return "application/octet-stream";

        }

    }

}
