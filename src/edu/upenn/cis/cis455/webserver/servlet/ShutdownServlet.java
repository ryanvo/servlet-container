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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Map;

public class ShutdownServlet implements HttpServlet {

    private static Logger log = LogManager.getLogger(ShutdownServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";
    private Map<String, String> initParams;
    private ServletContext context;
    private String servletName;

    private ConnectionManager manager;

    @Override
    public void init(ServletConfig config) {

        Enumeration paramNames = config.getInitParameterNames();
        while(paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

        this.context = config.getServletContext();
        this.servletName = config.getServletName();
        this.manager = (ConnectionManager) context.getAttribute("ConnectionManager");
    }

    @Override
    public void destroy() {



    }

    /**
     * closes the server socket so that no new connections are accepted and then issues a
     * stop to the manager
     * @param response associated with the stop request
     */
    public void doGet(HttpRequest request, HttpResponse response) {

        String SHUTDOWN_MESSAGE = "<html><body>Shutting down...</body></html>";

        log.info(getServletName() + " Serving Shutdown Request");

        manager.shutdown();

        response.setVersion(HTTP_VERSION);
        response.setStatusCode("200");
        response.setErrorMessage("OK");
        response.setContentType("text/html");
        response.setContentLength(SHUTDOWN_MESSAGE.length());

        try (ChunkedWriter writer = response.getWriter()) {
            log.debug(response.getStatusAndHeader());
            writer.println(response.getStatusAndHeader());
            writer.println(SHUTDOWN_MESSAGE);
        } catch (IOException e) {
            //TODO
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
