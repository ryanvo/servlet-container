package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ShutdownServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(ShutdownServlet.class);

    private Map<String, String> initParams = new HashMap<>();
    private ServletContext context;
    public String name;
    public ServletConfig config;

    private Container container;

    @Override
    public void init(ServletConfig config) throws ServletException {
        Enumeration paramNames = config.getInitParameterNames();
        while(paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

        this.context = config.getServletContext();
        this.name = config.getServletName();
        this.container = (Container) context.getAttribute("Container");
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException  {

        log.info(getServletName() + " Serving Shutdown Request");

        container.shutdown();

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
