package edu.upenn.cis.cis455.webserver.engine.http;

import edu.upenn.cis.cis455.webserver.engine.ServletContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class HttpSession {

    private ServletContext context;
    private Map<String, Object> attributes;

    private Date creationTime;

    public HttpSession(ServletContext context) {
        this.context = context;
        this.attributes = new ConcurrentHashMap<>();
        this.creationTime = new Date();

    }

    public long getCreationTime() {
        return creationTime.getTime();
    }

    public String getId() {
        return null;
    }

    public long getLastAccessedTime() {
        return 0;
    }

    public ServletContext getServletContext() {
        return context;
    }


    public void setMaxInactiveInterval(int i) {

    }

    public int getMaxInactiveInterval() {
        return 0;
    }


    public Object getAttribute(String s) {
        return attributes.get(s);
    }


    public Enumeration getAttributeNames() {
        Set<String> keys = attributes.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }


    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    public void removeAttribute(String s) {
        attributes.remove(s);
    }


    public void invalidate() {

    }

}
