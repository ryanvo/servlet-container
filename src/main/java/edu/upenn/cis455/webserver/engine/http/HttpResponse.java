package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.engine.http.io.Buffer;
import edu.upenn.cis455.webserver.engine.http.io.ChunkedResponseBuffer;
import edu.upenn.cis455.webserver.engine.http.io.ResponseBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class HttpResponse implements HttpServletResponse {

    private static Logger log = LogManager.getLogger(HttpResponse.class);

    private String HTTP = "HTTP/1.1";
    private String characterEncoding = "ISO-8859-1";
    private Locale locale = Locale.getDefault();

    private int statusCode = 200;
    private String statusMessage = "OK";
    private String contentType;

    private int contentLength = -1;
    private int bufferSize = 4096;
    private boolean isCommitted = false;

    private List<Cookie> cookies = new ArrayList<>();
    private Map<String, List<Long>> dateHeaders = new HashMap<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, List<Integer>> intHeaders = new HashMap<>();
    private Buffer msgBodyBuffer;
    private PrintWriter writerBuffer;

    public Buffer getMsgBodyBuffer() {
        if (writerBuffer != null) {
            writerBuffer.flush();
        }
        return msgBodyBuffer;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }


    public HttpResponse() {
        addHeader("Server", "ryanvo-server/1.5");
    }

    public void addHeader(String key, String value) {
        List<String> headerValues = headers.getOrDefault(key, new ArrayList<>());
        headerValues.add(value);
        headers.put(key, headerValues);
    }

    public void setHTTP(String HTTP) {
        this.HTTP = HTTP;
    }

    public String getProtocol() {
        return HTTP;
    }

    public int getContentLength() {
        return contentLength;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return statusMessage;
    }


    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Buffer getBuffer() {
        if (msgBodyBuffer == null) {
            getOutputStream();
        }

        return msgBodyBuffer;
    }

    @Override
    public void setIntHeader(String s, int i) {
        if (isCommitted()) {
            return;
        }

        List<Integer> intValues = new ArrayList<>();
        intValues.add(i);
        intHeaders.put(s, intValues);
    }

    @Override
    public void addIntHeader(String s, int i) {
        if (isCommitted()) {
            return;
        }
        List<Integer> intValues = intHeaders.getOrDefault(s, new ArrayList<>());
        intValues.add(i);
        intHeaders.put(s, intValues);
    }

    @Override
    public void setStatus(int i) {
        this.statusCode = i;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return dateHeaders.containsKey(name) || headers.containsKey(name) || intHeaders.containsKey(name);
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setBufferSize(int i) {
        if (isCommitted) {
            throw new IllegalStateException();
        }
        this.bufferSize = i;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        isCommitted = true;

        if (writerBuffer != null) {
            writerBuffer.flush();
        }
    }

    @Override
    public void resetBuffer() {
        msgBodyBuffer.clear();
    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    @Override
    public void reset() {

        if (isCommitted()) {
            throw new IllegalStateException();
        }

        resetBuffer();
        setStatus(200);
        statusMessage = "OK";
        cookies.clear();
        dateHeaders.clear();
        headers.clear();
        intHeaders.clear();
    }

    public void clear() {
        msgBodyBuffer = null;
        setStatus(200);
        statusMessage = "OK";
        bufferSize = 4096;
        HTTP = "HTTP/1.1";
        contentType = null;
        contentLength = -1;
        cookies.clear();
        dateHeaders.clear();
        headers.clear();
        intHeaders.clear();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void setCharacterEncoding(String s) {
        this.characterEncoding = s;
    }

    @Override
    public PrintWriter getWriter() {
        if (writerBuffer == null && msgBodyBuffer != null) {
            throw new IllegalStateException();
        }


        if (msgBodyBuffer == null && writerBuffer == null) {
            writerBuffer = new PrintWriter(getOutputStream());
        }

        return writerBuffer;
    }

    @Override
    public ServletOutputStream getOutputStream() {

        if (msgBodyBuffer == null) {

            if (!HTTP.endsWith("1")) {
                msgBodyBuffer = new ResponseBuffer(bufferSize);
            } else {
                msgBodyBuffer = new ChunkedResponseBuffer(bufferSize);
            }

        }

        return msgBodyBuffer.toServletOutputStream();
    }


    @Override
    public void sendError(int code, String msg)  {

        if (isCommitted()) {
            throw new IllegalStateException();
        }

        clear();
        isCommitted = true;
        statusMessage = msg;
        statusCode = code;

        getWriter().println(String.format("%d %s", code, msg));

    }

    @Override
    public void sendError(int i) {

        if (isCommitted()) {
            throw new IllegalStateException();
        }

        switch (i) {
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                sendError(i, "Internal Server Error");
                break;
            case HttpServletResponse.SC_NOT_FOUND:
                sendError(i, "Not Found");
                break;
            case HttpServletResponse.SC_FORBIDDEN:
                sendError(i, "Forbidden");
                break;
            case HttpServletResponse.SC_PRECONDITION_FAILED:
                sendError(i, "Precondition Failed");
                break;
            case HttpServletResponse.SC_UNAUTHORIZED:
                sendError(i, "Unauthorized");
                break;
            case HttpServletResponse.SC_NOT_MODIFIED:
                sendError(i, "Not Modified");
                break;
            default:
                sendError(i, "Bad Request");
                break;
        }
    }

    @Override
    public void sendRedirect(String s) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        resetBuffer();
        setStatus(SC_FOUND);
        statusMessage = "Found";
        addHeader("Location", s);
        flushBuffer();
    }

    @Override
    public void setDateHeader(String key, long val) {
        ArrayList<Long> dateValues = new ArrayList<>();
        dateValues.add(val);
        dateHeaders.put(key, dateValues);
    }

    @Override
    public void addDateHeader(String s, long l) {
        if (isCommitted()) {
            return;
        }

        List<Long> dateValues = dateHeaders.getOrDefault(s, new ArrayList<>());
        dateValues.add(l);
    }

    @Override
    public void setHeader(String key, String val) {
        if (isCommitted()) {
            return;
        }
        ArrayList<String> headerValues = new ArrayList<>();
        headerValues.add(val);
        headers.put(key, headerValues);
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    /* Deprecated methods */
    @Override
    @Deprecated
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    @Deprecated
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    @Deprecated
    public void setStatus(int i, String s) {
        this.statusCode = i;
        this.statusMessage = s;
    }
}
