package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.engine.io.ChunkedWriter;
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

public class ControlServlet implements HttpServlet {

    private static Logger log = LogManager.getLogger(ControlServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";
    private ConnectionManager manager;
    private Map<String, String> initParams;

    public ControlServlet(ConnectionManager manager) {
        this.manager = manager;
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

    }




    public void doGet(HttpRequest request, HttpResponse response) {


        try {
            response.setVersion(HTTP_VERSION);
            response.setStatusCode("200");
            response.setErrorMessage("OK");
            response.setContentType("text/html");
            response.addHeader("Transfer-Encoding", "chunked");

            ChunkedWriter writer = response.getWriter();

            writer.println(response.getStatusAndHeader());

            writer.write("<html><body><h1>Control Panel</h1>");
            writer.write("<p><h2>Thread &nbsp; &nbsp; &nbsp; &nbsp;Running</h2></p>");

            for (Map.Entry<Long, String> status : manager.getStatus().entrySet()) {
                long threadId = status.getKey();
                writer.write("<p>");
                writer.write(String.valueOf(threadId));
                writer.write("&nbsp; &nbsp; &nbsp; &nbsp &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
                        " &nbsp; &nbsp;");
                writer.write(status.getValue());
            }

            writer.write("<p><a href=\"/shutdown/\">Shutdown</a></p></body></html>");

            writer.close();

            log.info(getServletName() + " Serving Control Page Request");

            log.debug(response.getStatusAndHeader());


            log.info("Wrote Control Page Response to Socket");
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public void doHead(HttpRequest req, HttpResponse resp) {

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


}
