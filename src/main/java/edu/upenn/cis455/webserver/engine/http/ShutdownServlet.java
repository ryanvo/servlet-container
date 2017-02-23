package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis455.webserver.engine.AppContext;
import edu.upenn.cis455.webserver.engine.ServletConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class ShutdownServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(ShutdownServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";
    private Map<String, String> initParams;
    private ServletContext context;
    public String servletName;

    private ConnectionManager manager;

    @Override
    public void init(javax.servlet.ServletConfig config) throws ServletException {
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


    public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException  {

        String SHUTDOWN_MESSAGE = "<html><body>Shutting down...</body></html>";

        log.info(getServletName() + " Serving Shutdown Request");


        response.setStatus(200, "OK");
//        response.setContentType("text/html");
//        response.setContentLength(SHUTDOWN_MESSAGE.length());
                response.setContentLength(-1);

        response.setHeader("Connection", "close");

//        response.getWriter().println(SHUTDOWN_MESSAGE);

        response.flushBuffer();

        manager.shutdown();

    }
//
//    @Override
//    public void doHead(HttpRequest req, HttpResponse resp)  throws ServletException {
//
//    }
//
//    @Override
//    public void doPost(HttpRequest req, HttpResponse resp)  throws ServletException  {
//    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public AppContext getServletContext() {
        return null;
    }

    @Override
    public String getServletName() {
        return null;
    }


}
