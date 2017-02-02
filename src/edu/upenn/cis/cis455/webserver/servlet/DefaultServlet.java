package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.container.ServletContext;
import edu.upenn.cis.cis455.webserver.container.HttpConnectionManager;
import edu.upenn.cis.cis455.webserver.container.ServletConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultServlet implements HttpServlet {

    private static Logger log = LogManager.getLogger(DefaultServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";

    private final String rootDirectory;
    private HttpConnectionManager manager;

    /**
     * @param rootDirectory path to the www folder
     * @param manager needed to shutdown and get status of requests in thread pool
     */
    public DefaultServlet(String rootDirectory, HttpConnectionManager manager) {
        this.rootDirectory = rootDirectory;
        this.manager = manager;
    }

    @Override
    public void init(ServletConfig config) {

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
        manager.update(Thread.currentThread().getId(), request.getRequestURI().toString());

        log.info(String.format("Thread ID %d is Serving URI %s", Thread.currentThread().getId(),
                request.getRequestURI()));

//        switch (request.getType()) {
//            case "get":
//                doGet(request, response);
//                break;
//            case "control":
//                doControl(response);
//                break;
//            case "shutdown":
//                doShutdown(response);
//                break;
//            default:
//                log.error("DefaultServlet Did Not Recognize Request Type");
//                manager.update(Thread.currentThread().getId(), "waiting");
//                throw new IllegalStateException();
//        }

        doGet(request, response);

        manager.update(Thread.currentThread().getId(), "waiting");
    }


    public void doGet(HttpRequest request, HttpResponse response) {

        String NOT_FOUND_MESSAGE = "<html><body><h1>404 File Not Found</h1></body></html>";
        File fileRequested = new File(rootDirectory + request.getRequestURI().getPath());

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
                log.info("Not Found Error Sent to Client" + request.getRequestURI().getPath());
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

    public void doControl(HttpResponse response) {

        log.info("DefaultServlet Serving Control Page Request");
        String controlPageHtml = manager.getHtmlResponse();
        response.setVersion(HTTP_VERSION);
        response.setStatusCode("200");
        response.setErrorMessage("OK");
        response.setContentType("text/html");
        response.setContentLength(controlPageHtml.length());

        log.debug(response.getStatusAndHeader());

        PrintWriter writer = new PrintWriter(response.getOutputStream(), true);
        writer.println(response.getStatusAndHeader());
        writer.println(manager.getHtmlResponse());

        log.info("Wrote Control Page Response to Socket");
    }

    /**
     * closes the server socket so that no new connections are accepted and then issues a
     * shutdown to the manager
     * @param response associated with the shutdown request
     */
    public void doShutdown(HttpResponse response) {

        String SHUTDOWN_MESSAGE = "<html><body>Server shutting down...</body></html>";

        log.info("DefaultServlet Serving Shutdown Request");

//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//            log.error("Could not close socket. Server will not shutdown");
//        }
//        log.info("Server Socket Closed");
//        manager.issueShutdown();
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
