package edu.upenn.cis455.webserver.servlet.http;

import edu.upenn.cis455.webserver.engine.ApplicationContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ConnectionSession implements HttpSession {

    private ApplicationContext context;
    private Map<String, Object> attributes;

    private Date creationTime;

    public ConnectionSession(ApplicationContext context) {
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

    public ApplicationContext getServletContext() {
        return context;
    }


    public void setMaxInactiveInterval(int i) {

    }


    public int getMaxInactiveInterval() {
        return 0;
    }



    /**
     * javax.servlet.http.ConnectionSession API
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
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }


    @Override
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

    @Override @Deprecated
    public void removeValue(String s) {}

    @Override @Deprecated
    public void putValue(String s, Object o) {}

    @Override @Deprecated
    public String[] getValueNames() {return null; }

}
