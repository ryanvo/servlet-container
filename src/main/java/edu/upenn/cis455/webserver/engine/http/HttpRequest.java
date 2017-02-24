package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.engine.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.Principal;
import java.util.*;

public class HttpRequest implements HttpServletRequest {

    private static Logger log = LogManager.getLogger(HttpRequest.class);

    private String uri;
    private String method;
    private InputStream in;

    private String characterEncoding = "ISO-8859-1";
    private Locale locale = null;
    private String contentType = null;
    private boolean requestedSessionIdFromCookie = false;
    private boolean requestedSessionIdFromURL = false;

    private String pathInfo;
    private String servletPath;
    private String queryString;

    private BufferedReader reader = null;
    private StringBuffer requestURL = null;

    private int contentLength = -1; /* -1 indicates value has not been set */
    private String requestedSessionId = null;
    private List<Cookie> cookies = new ArrayList<>();
    private Map<String, Object> attributes = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, Long> dateHeaders = new HashMap<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private String protocol;
    private String serverName;
    private int serverPort;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String localName;
    private String localAddr;
    private SessionManager sessionManager;
    private boolean hasAccessedSession = false;


    public void setContext(ServletContext context) {
        this.context = context;
    }

    private ServletContext context;

    private int localPort;

    public void setParameters(Map<String, List<String>> params) {
        parameters = params;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setSessionManager(SessionManager manager) {
        this.sessionManager = manager;
    }

    public void setInputStream(InputStream in) {
        this.in = in;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public boolean hasAccessedSession() {
        return hasAccessedSession;
    }

    /**
     * javax.http.http API
     */

    @Override
    public String getAuthType() {
        return "BASIC";
    }

     @Override
    public Cookie[] getCookies() {
        return (cookies.size() > 0) ? cookies.toArray(new Cookie[cookies.size()]) : new Cookie[0];
    }

     @Override
    public long getDateHeader(String s) {
        return (dateHeaders.containsKey(s)) ? dateHeaders.get(s) : -1;
    }

     @Override
    public String getHeader(String s) {
        if (headers.containsKey(s)) {
            return headers.get(s).get(0);
        } else {
            return null;
        }
    }

    @Override
    public HttpSession getSession(boolean b) {

        /* Check for session cookie */
        Cookie sessionCookie = null;
        for (Cookie c : cookies) {
            if (c.getName().equals("JSESSIONID")) {
                sessionCookie = c;
                break;
            }
        }

        /* If session cookie found, make sure the session is still valid */
        if (!b && sessionCookie != null && !sessionManager.isValid(sessionCookie.getValue())) {
            hasAccessedSession = true;

            sessionManager.invalidateSession(sessionCookie.getValue());
            log.info("Invalidated session: id:" + sessionCookie.getValue());

            return null;
        }

        /* If there was not session cookie, but a new session not to be created */
        if (sessionCookie == null && !b) {
            return null;
        }

        /* Retrieve the existing session or create a new one */
        ConnectionSession session = null;
        if (sessionCookie == null) {
            session = sessionManager.createSession(context);
        } else {

            session = sessionManager.findSession(sessionCookie.getValue());

            if (session == null) {

                session = sessionManager.createSession(context);

            } else if (!sessionManager.isValid(session)) {

                sessionManager.invalidateSession(session.getId());
                log.info("Invalidated session: id:" + session.getId());

                session = sessionManager.createSession(context);

            }
        }

        log.info("Created new session: id:" + session.getId());

        hasAccessedSession = true;
        session.markAccessed();
        requestedSessionId = session.getId();

        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public BufferedReader getReader() {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(in));
        }

        return reader;
    }

    @Override
    public Enumeration getHeaders(String s) {
        return Collections.enumeration(headers.get(s));
    }

     @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

     @Override
    public int getIntHeader(String s) {
        return Integer.parseInt(headers.get(s).get(0));
    }

     @Override
    public String getMethod() {
        return method;
    }

     @Override
    public String getPathInfo() {
        return pathInfo;
    }

     @Override
    public String getPathTranslated() {
        return null;
    }

     @Override
    public String getContextPath() {
        return context.getRealPath("/");
    }

     @Override
    public String getQueryString() {
        return queryString;
    }

     @Override
    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    @Override
    public String getRequestURI() {
        return uri;
    } //TODO

     @Override
    public StringBuffer getRequestURL() {
        return requestURL;
    } //TODO

     @Override
    public String getServletPath() {
        return servletPath;
    } //TODO

     @Override
    public boolean isRequestedSessionIdValid() {
        return requestedSessionId != null;
    }

     @Override
    public boolean isRequestedSessionIdFromCookie() {
        return requestedSessionIdFromCookie;
    }

     @Override
    public boolean isRequestedSessionIdFromURL() {
        return requestedSessionIdFromURL;
    }

     @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

     @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

     @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

     @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        characterEncoding = s;
    }

     @Override
    public int getContentLength() {
        return contentLength;
    }

     @Override
    public String getContentType() {
        return contentType;
    }

     @Override
    public String getParameter(String s) {
        if (parameters.containsKey(s)) {

            return parameters.get(s).get(0);

        } else {
            return null;
        }

    }

     @Override
    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

     @Override
    public String[] getParameterValues(String s) {
        return parameters.get(s).toArray(new String[parameters.get(s).size()]);
    }

     @Override
    public Map getParameterMap() {
        return parameters;
    }

     @Override
    public String getProtocol() {
        return protocol;
    }

     @Override
    public String getScheme() {
        return "http";
    }

     @Override
    public String getServerName() {
        return serverName;
    }

     @Override
    public int getServerPort() {
        return serverPort;
    }

     @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

     @Override
    public String getRemoteHost() {
        return remoteHost;
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
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

     @Override
    public int getRemotePort() {
        return remotePort;
    }

     @Override
    public String getLocalName() {
        return localName;
    }

     @Override
    public String getLocalAddr() {
        return localAddr;
    }

     @Override
    public int getLocalPort() {
        return localPort;
    }

    /* setters */

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }


    public void setRequestedSessionIdFromURL(boolean flag) {
        this.requestedSessionIdFromURL = flag;
    }

    public void setRequestedSessionIdFromCookie(boolean flag) {
        this.requestedSessionIdFromCookie = flag;
    }


    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = new StringBuffer(requestURL);
    }

    /**
     * Not implemented
     */


    @Override /* server only supports getReader */
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }


    @Override @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override /* HTTPS not supported */
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

}
