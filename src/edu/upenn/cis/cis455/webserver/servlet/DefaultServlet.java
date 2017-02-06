package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.engine.io.ChunkedWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;

public class DefaultServlet implements HttpServlet {

    private static Logger log = LogManager.getLogger(DefaultServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";
    private final String rootDirectory;
    private Map<String, String> initParams;

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

        String NOT_FOUND_MESSAGE = "<html><body><h1>404 File Not Found</h1></body></html>";
        File fileRequested = new File(rootDirectory + request.getRequestURI());

        try (ChunkedWriter writer = response.getWriter()) {

            if (fileRequested.canRead() && fileRequested.isDirectory()) {

                log.info(String.format("DefaultServlet Serving GET Request for Directory %s",
                        fileRequested.getName()));

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.setContentType("text/html");

                StringBuilder fileDirectoryListingHtml = new StringBuilder();
                fileDirectoryListingHtml.append("<html><body>");

                for(File file : fileRequested.listFiles()) {
                    Path rootPath = Paths.get(rootDirectory);
                    Path fileAbsolutePath = Paths.get(file.getAbsolutePath());
                    Path relativePath = rootPath.relativize(fileAbsolutePath);
                    fileDirectoryListingHtml.append(String.format("<p><a href=\"%s\">%s</a></p>",
                            relativePath.toString(), file.getName()));
                }

                fileDirectoryListingHtml.append("</html></body>");

                writer.println(response.getStatusAndHeader());
                writer.write(fileDirectoryListingHtml.toString());
                writer.flush();

                log.info(String.format("Directory Listing of %s Sent to Client", fileRequested
                        .getName()));

            } else if (fileRequested.canRead()) {

                log.info(String.format("DefaultServlet Serving GET Request for %s", fileRequested
                        .getName()));

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");
                response.setContentType(probeContentType(fileRequested.getPath()));
                int contentLength = Long.valueOf(fileRequested.length()).intValue();
                response.setContentLength(contentLength);
                writer.println(response.getStatusAndHeader());
                writer.flush();

                /* Send file as binary to output stream */
                InputStream is = new FileInputStream(fileRequested);
                byte[] buf = new byte[contentLength];
                int bytesRead;
                while ((bytesRead = is.read(buf, 0, buf.length)) > 0) {
                    response.getOutputStream().write(buf, 0, bytesRead);
                    response.getOutputStream().flush();

//                    writer.write(new String(buf), 0, bytesRead);
                }


                log.info(String.format("%s Sent to Client", fileRequested.getName()));

            } else {

                log.info(String.format("%s Not found", fileRequested.getName()));

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("404");
                response.setErrorMessage("Not Found");
                response.setContentType("text/html");
                response.setContentLength(NOT_FOUND_MESSAGE.length());

                writer.println(response.getStatusAndHeader());
                writer.flush();

                writer.write(NOT_FOUND_MESSAGE);
                writer.flush();

                log.info("Not Found Error Sent to Client" + request.getRequestURI());
            }

            log.debug(response.getStatusAndHeader());

        } catch (IOException e) {
            log.error("Could Not Write GET Response to Socket", e);

        }
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