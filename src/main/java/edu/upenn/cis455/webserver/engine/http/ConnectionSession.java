package edu.upenn.cis455.webserver.engine.http;

import edu.upenn.cis455.webserver.engine.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an HTTP session
 * @author rtv
 */
public class ConnectionSession implements HttpSession {

    private static Logger log = LogManager.getLogger(HttpSession.class);

    private ServletContext context;
    private Map<String, Object> attributes;

    private ZonedDateTime creationTime;
    private ZonedDateTime lastAccessedTime;
    private int timeoutInterval = -1;
    private boolean isInvalidated = false;
    private final String id;
    private boolean isNew = true;
    private SessionManager manager;

    public ConnectionSession(String id, ServletContext context, SessionManager manager) {
        this.manager = manager;
        this.id = id;
        this.context = context;
        this.attributes = new ConcurrentHashMap<>();
        this.creationTime = ZonedDateTime.now(ZoneId.of("GMT"));
    }

    public void markAccessed() {
        isNew = false;
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
        return id;
    }


    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime.toEpochSecond();
    }


    @Override
    public ServletContext getServletContext() {
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

        manager.invalidateSession(id);
        isInvalidated = true;

        log.info("Invalidated Session: id:" + id);
    }

    @Override
    public boolean isNew() {
        return isNew;
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
