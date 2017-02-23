package edu.upenn.cis455.webserver.http;

import edu.upenn.cis455.webserver.engine.ApplicationContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ConnectionSession implements HttpSession {

    private ApplicationContext context;
    private Map<String, Object> attributes;

    private ZonedDateTime creationTime;
    private ZonedDateTime lastAccessedTime;
    private int timeoutInterval = -1;
    private boolean isInvalidated = false;
    private final int id;

    public ConnectionSession(int id, ApplicationContext context) {
        this.id = id;
        this.context = context;
        this.attributes = new ConcurrentHashMap<>();
        this.creationTime = ZonedDateTime.now(ZoneId.of("GMT"));
        this.lastAccessedTime = creationTime;
    }



    public void markAccessed() {

        lastAccessedTime = ZonedDateTime.now(ZoneId.of("GMT"));

    }

    /**
     * javax.http.http.ConnectionSession API
     */


    @Override
    public long getCreationTime() {
        return creationTime.toEpochSecond();
    }

    @Override
    public String getId() {
        return null;
    }


    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime.toEpochSecond();
    }


    @Override
    public ApplicationContext getServletContext() {
        return context;
    }


    @Override
    public void setMaxInactiveInterval(int i) {
        timeoutInterval = i;
    }

    @Override
    public int getMaxInactiveInterval() {

        return timeoutInterval;

    }

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
        if (isInvalidated) {
            throw new IllegalStateException();
        }

        isInvalidated = true;
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
