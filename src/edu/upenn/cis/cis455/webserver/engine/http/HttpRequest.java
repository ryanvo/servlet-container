package edu.upenn.cis.cis455.webserver.engine.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

public class HttpRequest {

    private static Logger log = LogManager.getLogger(HttpRequest.class);

    private URI uri;
    private String type;

    public HttpRequest reset() {

        return this;
    }

//    public void setMethod(String method) {
//        this.method = method;
//    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return type of request e.g. GET, POST...
     */
    public String getType() {
        return type;
    }

//    @Override
//    public String getAuthType() {
//        return null;
//    }
//
//    @Override
//    public Cookie[] getCookies() {
//        return new Cookie[0];
//    }
//
//    @Override
//    public long getDateHeader(String s) {
//        return 0;
//    }
//
//    @Override
//    public String getHeader(String s) {
//        return null;
//    }
//
//    @Override
//    public Enumeration getHeaders(String s) {
//        return null;
//    }
//
//    @Override
//    public Enumeration getHeaderNames() {
//        return null;
//    }
//
//    @Override
//    public int getIntHeader(String s) {
//        return 0;
//    }
//
//    @Override
//    public String getMethod() {
//        return null;
//    }
//
//    @Override
//    public String getPathInfo() {
//        return null;
//    }
//
//    @Override
//    public String getPathTranslated() {
//        return null;
//    }
//
//    @Override
//    public String getContextPath() {
//        return null;
//    }
//
//    @Override
//    public String getQueryString() {
//        return null;
//    }
//
//    @Override
//    public String getRemoteUser() {
//        return null;
//    }
//
//    @Override
//    public boolean isUserInRole(String s) {
//        return false;
//    }
//
//    @Override
//    public Principal getUserPrincipal() {
//        return null;
//    }
//
//    @Override
//    public String getRequestedSessionId() {
//        return null;
//    }
//
    /**
     * @return uri requested in status line
     */
    public URI getRequestURI() {
        return uri;
    }

//    @Override
//    public StringBuffer getRequestURL() {
//        return null;
//    }
//
//    @Override
//    public String getServletPath() {
//        return null;
//    }
//
//    @Override
//    public HttpSession getSession(boolean b) {
//        return null;
//    }
//
//    @Override
//    public HttpSession getSession() {
//        return null;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdValid() {
//        return false;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromCookie() {
//        return false;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromURL() {
//        return false;
//    }

}
