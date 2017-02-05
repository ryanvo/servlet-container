package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
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

    /**
     * Sends the request to the appropriate handling method
     * @param request
     * @param response
     */
    public void service(HttpRequest request, HttpResponse response) {
//        manager.update(Thread.currentThread().getId(), request.getRequestURI().toString());

        log.info(String.format("Thread ID %d is Serving URI %s", Thread.currentThread().getId(),
                request.getRequestURI()));

//        switch (request.getMethod()) {
//            case "get":
//                doGet(request, response);
//                break;
//            case "control":
//                doControl(response);
//                break;
//            case "stop":
//                doShutdown(response);
//                break;
//            default:
//                log.error("DefaultServlet Did Not Recognize Request Type");
//                manager.update(Thread.currentThread().getId(), "waiting");
//                throw new IllegalStateException();
//        }

        doGet(request, response);

//        manager.update(Thread.currentThread().getId(), "waiting");
    }


    public void doGet(HttpRequest request, HttpResponse response) {

        String NOT_FOUND_MESSAGE = "<html><body><h1>404 File Not Found</h1></body></html>";
        File fileRequested = new File(rootDirectory + request.getRequestURI());

        try (PrintWriter writer = new PrintWriter(response.getOutputStream())) {

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

                response.setContentLength(fileDirectoryListingHtml.length());

                writer.println(response.getStatusAndHeader());
                writer.flush();
                writer.print(fileDirectoryListingHtml.toString());

                log.info(String.format("Directory Listing of %s Sent to Client", fileRequested
                        .getName()));

            } else if (fileRequested.canRead()) {

                log.info(String.format("DefaultServlet Serving GET Request for %s", fileRequested
                        .getName()));

                response.setVersion(HTTP_VERSION);
                response.setStatusCode("200");
                response.setErrorMessage("OK");

                try {
                    response.setContentType(Files.probeContentType(fileRequested.toPath()));
                } catch (IOException e) {
                    log.error("Error Reading File to Determine Content-Type", e);
                }
                response.setContentLength(Long.valueOf(fileRequested.length()).intValue());

                writer.println(response.getStatusAndHeader());
                writer.flush();

                Files.copy(fileRequested.toPath(), response.getOutputStream());
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

                writer.println(NOT_FOUND_MESSAGE);
                log.info("Not Found Error Sent to Client" + request.getRequestURI());
            }

            log.debug(response.getStatusAndHeader());

        } catch (IOException e) {
            log.error("Could Not Write GET Response to Socket", e);

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

    /**
     * closes the server socket so that no new connections are accepted and then issues a
     * stop to the manager
     * @param response associated with the stop request
     */
    public void doShutdown(HttpResponse response) {

        String SHUTDOWN_MESSAGE = "<html><body>ConnectionHandler shutting down...</body></html>";

        log.info("DefaultServlet Serving Shutdown Request");

//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//            log.error("Could not close socket. ConnectionHandler will not stop");
//        }
//        log.info("ConnectionHandler Socket Closed");
//        manager.shutdown();
//
//        response.setVersion(HTTP_VERSION);
//        response.setStatusCode("200");
//        response.setErrorMessage("OK");
//        response.setContentType("text/html");
//        response.setContentLength(SHUTDOWN_MESSAGE.length());
//
//
//        try(PrintWriter writer = new PrintWriter(response.getOutputStream(), true)) {
//
//            log.debug(response.getStatusAndHeader());
//
//            writer.println(response.getStatusAndHeader());
//            writer.println(SHUTDOWN_MESSAGE);
//
//        }

    }

}
