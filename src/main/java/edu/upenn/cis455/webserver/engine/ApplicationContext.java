package edu.upenn.cis455.webserver.engine;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ApplicationContext implements ServletContext {

    private static Logger log = LogManager.getLogger(ApplicationContext.class);

    private static int MAJOR_VERSION = 2;
    private static int MINOR_VERSION = 4;

    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    private Map<String, String> contextParams;
    private String realPath;
    private String name;

    public ApplicationContext(ServletContextBuilder builder) {
        name = builder.name;
        contextParams = new ConcurrentHashMap<>(builder.getContextParams());
        realPath = builder.getRealPath();
    }

    /**
     * My Setters
     */

    public void setInitParam(String name, String value) {
        contextParams.put(name, value);
    }


    /**
     * javax.servlet.ApplicationContext API
     */

    @Override
    public String getInitParameter(String s) {
        return contextParams.get(s);
    }

    @Override
    public Enumeration getInitParameterNames() {
        Set<String> keys = contextParams.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }

    @Override
    public void log(String s) {
        log.error(s);
    }

    @Override
    public void log(Exception e, String s) {
        log.error(s, e);
    }

    @Override
    public void log(String s, Throwable throwable) {
        log.error(s, throwable);
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public Enumeration getAttributeNames() {
        Set<String> keys = attributes.keySet();
        return new Vector<>(keys).elements();
    }

    @Override //TODO confirm
    public String getRealPath(String s) {

        return realPath;

    }

    @Override //TODO
    public ApplicationContext getContext(String s) {
        return null;
    }

    @Override
    public String getServerInfo() {
        return "ryanvo-server/2.0";
    }

    /**
     * @return display-name element in web.xml
     */
    @Override
    public String getServletContextName() {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    @Override
    public String getMimeType(String filePath) {

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

        } else if (filePath.endsWith(".jsp")) {

            return "text/html";
        } else {

            return "application/octet-stream";
        }
    }


    @Override
    public InputStream getResourceAsStream(String s) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(s);
    }

    /**
     * Not Implemented
     */

    @Override @Deprecated
    public Enumeration getServlets() {
        return null;
    }

    @Override @Deprecated
    public Enumeration getServletNames() {
        return null;
    }

    @Override @Deprecated
    public Servlet getServlet(String s) throws ServletException {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String s) {
        return null;
    }

    @Override
    public Set getResourcePaths(String s) {
        return null;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

}
