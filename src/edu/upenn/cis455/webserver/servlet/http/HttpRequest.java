package edu.upenn.cis455.webserver.servlet.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.Principal;
import java.util.*;

public class HttpRequest implements HttpServletRequest {

    private static Logger log = LogManager.getLogger(HttpRequest.class);

    private String uri;
    private String method;
    private InputStream in;


//    private URI uri;

    private ConnectionSession session = null;
    private ServletContext context;
    private String characterEncoding = "ISO-8859-1";
    private Locale locale = null;
    private String contentType = null;
    private boolean requestedSessionIdFromCookie = false;
    private boolean requestedSessionIdFromURL = false;

    private String pathInfo;
    private String contextPath;
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

    private int localPort;

    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    public List<String> getHeaderValues(String key) {
        return headers.get(key);
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return method of request e.g. GET, POST...
     */
    public BufferedReader getReader() {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(in));
        }
        return reader;
    }

//    public InputStream getInputStream() {
//        return in;
//    }

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


    /**
     * javax.servlet.http API
     */

    @Override
    public String getAuthType() {
        return "BASIC";
    }

     @Override
    public Cookie[] getCookies() {
        return (cookies.size() > 0) ? cookies.toArray(new Cookie[cookies.size()]) : null;
    }

     @Override
    public long getDateHeader(String s) {
        return (dateHeaders.containsKey(s)) ? dateHeaders.get(s) : -1;
    }

     @Override
    public String getHeader(String s) {
        List<String> values = headers.get(s);
        if (values == null) {
            return null;
        } else {
            return values.get(0);
        }

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
        return contextPath;
    }

     @Override
    public String getQueryString() {
        return queryString;
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
    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    /**
     * @return uri requested in status line
     */
    public String getRequestURI() {
        return uri;
    }

     @Override
    public StringBuffer getRequestURL() {
        return requestURL;
    }

     @Override
    public String getServletPath() {
        return servletPath;
    }

    @Override
    public javax.servlet.http.HttpSession getSession(boolean b) {




//        if (isRequestedSessionIdValid()) {
//            return session;
//        }
//
//        if (!flag) {
//            return null;
//        }
//
//        session = new MyHttpSession();
//        requestedSessionId = session.getId();
//        context.getSessionManager().addSession(session);
//        return session;
        return null;
    }

     @Override
    public javax.servlet.http.HttpSession getSession() {
        return getSession(true);
    }

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

     @Override @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return false;
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

     @Override /* server only supports getReader */
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

     @Override
    public String getParameter(String s) {
        return parameters.get(s).get(0);
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

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
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

//    public void setBody(String body) throws UnsupportedEncodingException {
//        InputStream bodyStream = new ByteArrayInputStream(body.getBytes(getCharacterEncoding()));
//        reader = new BufferedReader(new InputStreamReader(bodyStream));
//    }

    public void setRequestURL(String requestURL) {
        this.requestURL = new StringBuffer(requestURL);
    }

}
