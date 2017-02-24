package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis455.webserver.engine.SessionManager;
import edu.upenn.cis455.webserver.engine.WebAppContainer;
import edu.upenn.cis455.webserver.engine.WebXmlHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to deploy a new web app instance based on fields
 * received in a POST request
 */
public class LauncherServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(LauncherServlet.class);

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();

        String xmlPath = URLDecoder.decode(req.getParameter("xmlPath"), "UTF-8");
        String warPath = URLDecoder.decode(req.getParameter("warPath"), "UTF-8");
        String contextPath = (req.getParameter("contextPath").isEmpty()) ?
                warPath : URLDecoder.decode(req.getParameter("contextPath"), "UTF-8");

        if (xmlPath == null || warPath == null) {
            writer.write("Invalid values. Please try again.");
            return;
        }


        try {
            addToClassPath(contextPath);
            addToClassPath(warPath);
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            log.error(e);
            return;
        }


        WebXmlHandler webXml = new WebXmlHandler(xmlPath);
        try {
            webXml.parse();
        } catch (SAXException e) {
            writer.write("Invalid XML configuration file.");
            return;
        }

        try {
            webAppContainer.startApp(contextPath, webXml);
        } catch (ReflectiveOperationException e) {
            writer.write("Unable to start app.");
            return;
        }

        writer.write("Successfully loaded app.");

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

    /**
     * Using URLClassLoader.addURL and reflection to access addURL method
     */
    public void addToClassPath(String path) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        URI uri = new File(path).toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{uri.toURL()});
    }

}
