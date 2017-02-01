package edu.upenn.cis.cis455.webserver.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public ServletContext() {
        attributes = new ConcurrentHashMap<>();
        initParams = new ConcurrentHashMap<>();
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

    /*Returns the name and version of the servlet container on which the servlet is running.
The form of the returned string is servername/versionnumber. For example, the JavaServer Web Development Kit may return the string JavaServer Web Dev Kit/1.0.

*/
    public String getServerInfo() {
        return null;
    }

    /*Returns the name of this web application corresponding to this ServletContext as specified in the deployment descriptor for this web application by the display-name element.
    */
    public String getServletContextName() {
        return null;
    }

    public ServletContext getContext(String s) {
        return null;
    }

    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

}
