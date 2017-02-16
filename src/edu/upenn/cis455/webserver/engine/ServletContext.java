package edu.upenn.cis455.webserver.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
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
public class ServletContext implements javax.servlet.ServletContext{

    private static Logger log = LogManager.getLogger(ServletContext.class);

    private Map<String, Object> attributes  = new ConcurrentHashMap<>();
    private Map<String, String> contextParams;
    private String realPath;

    public ServletContext(ServletContextBuilder builder) {
        contextParams = new ConcurrentHashMap<>(builder.getContextParams());
        realPath = builder.getRealPath();
    }

    /**
     *  My Setters
     */
    public void setInitParam(String name, String value) {
        contextParams.put(name, value);
    }


    /**
     * javax.servlet API
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

    }

    @Override
    public void log(String s, Throwable throwable) {

    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    public Enumeration getAttributeNames() {
        Set<String> keys = attributes.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }


    public String getRealPath(String s) {
        return realPath;
    }

    public void setRealPath(String s) {
        realPath = s;
    }

    @Override
    public ServletContext getContext(String s) {
        return null;
    }

    @Override
    public String getServerInfo() {
        return null;
    }

    @Override
    public String getServletContextName() {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
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

        } else {

            return "application/octet-stream";
        }
    }

    @Override
    public Set getResourcePaths(String s) {
        return null;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String s) {
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
    public Servlet getServlet(String s) throws ServletException {
        return null;
    }

    @Override
    public Enumeration getServlets() {
        return null;
    }

    @Override
    public Enumeration getServletNames() {
        return null;
    }

}
