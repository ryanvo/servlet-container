package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis455.webserver.engine.SessionManager;
import edu.upenn.cis455.webserver.engine.WebApp;
import edu.upenn.cis455.webserver.engine.WebAppContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ManageServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(ManageServlet.class);

    private String name;
    private Map<String, String> initParams = new HashMap<>();
    private ServletConfig config;
    private ServletContext context;

    private WebAppContainer webAppContainer;
    private ConnectionManager connectionManager;
    private SessionManager sessionManager;


    @Override
    public void init(ServletConfig config) throws ServletException {
        this.name = config.getServletName();
        this.config = config;
        this.context = config.getServletContext();

        Enumeration paramNames = config.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }


        webAppContainer = (WebAppContainer) config.getServletContext().getAttribute("Container");
        connectionManager = (ConnectionManager) config.getServletContext().getAttribute("ConnectionManager");
        sessionManager = (SessionManager) config.getServletContext().getAttribute("SessionManager");
    }

    @Override
    public void destroy() {


    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(200);
        response.setContentType("text/html");


        PrintWriter writer = response.getWriter();
        writer.print("<html><body><h1>Web Apps</h1>");
        writer.print("<p><h2>Thread &nbsp; &nbsp; &nbsp; &nbsp;Running</h2></p>");

        for (WebApp app : webAppContainer.getWebAppByName().values()) {
            writer.print("<p>" + app.getName() + "</p>");
            for (String servlet : app.getServlets().keySet()) {

                writer.print("<p>&nbsp; &nbsp; &nbsp;" + servlet + "</p>");

            }

        }



        log.info("Wrote Control Page Response to Socket");
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
