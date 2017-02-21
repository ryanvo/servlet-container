package edu.upenn.cis455.webserver.servlet.http;


import edu.upenn.cis455.webserver.servlet.io.Buffer;
import edu.upenn.cis455.webserver.servlet.io.ChunkedResponseBuffer;
import edu.upenn.cis455.webserver.servlet.io.ResponseBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.BufferUnderflowException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HttpResponse implements HttpServletResponse {

    private static Logger log = LogManager.getLogger(HttpResponse.class);

    private String HTTP = "HTTP/1.1";
    private String characterEncoding = "ISO-8859-1";
    private Locale locale = Locale.getDefault();

    private int statusCode = 200;
    private String errorMessage = "OK";
    private String contentType;

    private int contentLength = -1;
    private int bufferSize = 4096;

    private boolean isCommitted = false;
    private List<Cookie> cookies = new ArrayList<>();
    private Map<String, List<Long>> dateHeaders = new HashMap<>();


    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, List<Integer>> intHeaders = new HashMap<>();


    public Buffer getMsgBodyBuffer() {
        if (writerBuffer != null) {
            writerBuffer.flush();
        }
        return msgBodyBuffer;
    }

    private Buffer msgBodyBuffer;
    private PrintWriter writerBuffer;

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

    public String getHTTP() {
        return HTTP;
    }
    public int getContentLength() {
        return contentLength;
    }


    public void setMsgBodyBuffer(Buffer msgBodyBuffer) {
        this.msgBodyBuffer = msgBodyBuffer;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public Map<String, List<String>> getHeaders() {
        return headers;
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
        addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
    }

    @Override
    public boolean containsHeader(String name) {
        return dateHeaders.containsKey(name) || headers.containsKey(name) || intHeaders.containsKey(name);
    }

    @Override // TODO: 2/9/17  
    public String encodeURL(String s) {
        return null;
    }

    @Override // TODO: 2/9/17  
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

//        /* Close output stream that was used */
//        if (msgBodyBuffer != null && writerBuffer == null) {
//            msgBodyBuffer.flush();
//        } else if (writerBuffer != null)  {
//            writerBuffer.flush();
//        }


        if (writerBuffer != null) {
            writerBuffer.flush();
        }


    }

    @Override
    public void resetBuffer() {


    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    @Override
    public void reset() {
        resetBuffer();
        setStatus(-1);
//        setErrorMessage(null);
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

        if (msgBodyBuffer == null) {
            writerBuffer = new PrintWriter(getOutputStream());

        } else {

            throw new IllegalStateException();
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

        } else {
            throw new IllegalStateException();
        }
        return msgBodyBuffer.toServletOutputStream();
    }


    @Override
    public void sendError(int code, String msg) {

        if (isCommitted()) {
            throw new IllegalStateException();
        }

        isCommitted = true;
        reset();
        errorMessage = msg;
        statusCode = code;
        setContentLength(0);
    }

    @Override
    public void sendError(int i) throws IOException {

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

//        setStatus(SC_FOUND);
////        setErrorMessage("Found");
//        resetBuffer();
//        addHeader("Location", encodeURL(s));
//        flushBuffer();
//        isCommitted = true;
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
        this.errorMessage = s;
    }
}
