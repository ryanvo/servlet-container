package edu.upenn.cis455.webserver.servlet.http;

import edu.upenn.cis455.webserver.engine.ServletContext;

import javax.servlet.http.HttpSessionContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class HttpSession implements javax.servlet.http.HttpSession {

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



    /**
     * javax.servlet.http.HttpSession API
     */

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }



    @Override
    public Enumeration getAttributeNames() {
        Set<String> keys = attributes.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }


    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void putValue(String s, Object o) {

    }

    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public void removeValue(String s) {

    }


    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }


    /**
     * Not Implemented
     */

    @Override @Deprecated
    public Object getValue(String s) {
        return null;
    }

    @Override @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

}
