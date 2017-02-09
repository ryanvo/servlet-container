package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.PrintWriter;
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
    public void init(ServletConfig config)  throws ServletException  {

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


    public void doGet(HttpRequest request, HttpResponse response)  throws ServletException  {

        String SHUTDOWN_MESSAGE = "<html><body>Shutting down...</body></html>";

        log.info(getServletName() + " Serving Shutdown Request");

        manager.shutdown();

        response.setStatus(200, "OK");
        response.setContentType("text/html");
        response.setContentLength(SHUTDOWN_MESSAGE.length());

        PrintWriter writer = response.getWriter();
        log.debug(response.getStatusAndHeader());
        writer.println(response.getStatusAndHeader());
        writer.println(SHUTDOWN_MESSAGE);
        writer.flush();

    }

    @Override
    public void doHead(HttpRequest req, HttpResponse resp)  throws ServletException {

    }

    @Override
    public void doPost(HttpRequest req, HttpResponse resp)  throws ServletException  {
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
