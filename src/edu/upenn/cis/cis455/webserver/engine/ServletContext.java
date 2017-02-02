package edu.upenn.cis.cis455.webserver.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
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
public class ServletContext {

    private static Logger log = LogManager.getLogger(ServletContext.class);

    private Map<String, Object> attributes;
    private Map<String, String> initParams;

    public ServletContext(WebXmlHandler webXml) {
        attributes = new ConcurrentHashMap<>();
        initParams = new ConcurrentHashMap<>();

         /* Use context parameters to set the context obj */
        for (String param : webXml.getContextParams()) {
            initParams.put(param, webXml.getContextParamByKey(param));
        }

    }

    void setInitParam(String name, String value) {
        initParams.put(name, value);
    }

    public String getInitParameter(String s) {
        return initParams.get(s);
    }

    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }

    public void log(String s) {
        log.error(s);
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
        return null;
    }

    public String getServerInfo() {
        return null;
    }


    public String getServletContextName() {
        return null;
    }

    public ServletContext getContext(String s) {
        return null;
    }

//    @Override
    public int getMajorVersion() {
        return 0;
    }

//    @Override
    public int getMinorVersion() {
        return 0;
    }

//    @Override
    public String getMimeType(String s) {
        return null;
    }

//    @Override
    public Set getResourcePaths(String s) {
        return null;
    }

    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

//    @Override
    public InputStream getResourceAsStream(String s) {
        return null;
    }

//    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

//    @Override
    public RequestDispatcher getNamedDispatcher(String s) {
        return null;
    }

}
