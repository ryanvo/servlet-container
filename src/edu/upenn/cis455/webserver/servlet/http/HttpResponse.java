package edu.upenn.cis455.webserver.servlet.http;


import edu.upenn.cis455.webserver.servlet.io.ResponseBufferOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HttpResponse implements HttpServletResponse {

    private static Logger log = LogManager.getLogger(HttpResponse.class);

    private final static String HTTP_1_1 = "HTTP/1.1";
    private String characterEncoding = "ISO-8859-1";
    private final static DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
    private Locale locale = Locale.getDefault();

    private String url;

    private int statusCode;
    private String errorMessage;
    private String date;
    private String contentType;
    private int contentLength = -1;
    private int bufferSize;

    private boolean isCommitted = false;
    private List<Cookie> cookies = new ArrayList<>();
    private Map<String, List<Long>> dateHeaders = new HashMap<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, List<Integer>> intHeaders = new HashMap<>();

    private OutputStream socketOut;
    private ResponseBufferOutputStream msgBodyBuffer;
    private PrintWriter writerBuffer;

    public void addHeader(String key, String value) {
        List<String> headerValues = headers.getOrDefault(key, new ArrayList<>());
        headerValues.add(value);
        headers.put(key, headerValues);
    }

    public void setOutputStream(OutputStream out) {
        socketOut = out;
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

        /* Write status line, headers, and CRLF */
        socketOut.write(generateStatusAndHeaders().getBytes());
        socketOut.write("\n".getBytes());

        log.error("Just wrote:\n" + generateStatusAndHeaders());

        /* Write msg body to socket */
        if (msgBodyBuffer != null) {
            msgBodyBuffer.writeTo(socketOut);
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
        if (writerBuffer == null && msgBodyBuffer == null) {
            msgBodyBuffer = new ResponseBufferOutputStream(bufferSize);
            writerBuffer = new PrintWriter(msgBodyBuffer);
        } else if (writerBuffer == null) {
            throw new IllegalStateException();
        }
        return writerBuffer;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (msgBodyBuffer == null && writerBuffer == null) {
            msgBodyBuffer = new ResponseBufferOutputStream(bufferSize);
        } else if (msgBodyBuffer == null) {
            throw new IllegalStateException();
        }
        return msgBodyBuffer;
    }

    private String generateStatusAndHeaders() {
        StringBuilder sb = new StringBuilder();

        sb.append(HTTP_1_1).append(" ").append(statusCode).append(" ").append(errorMessage).append('\n');
        sb.append("Date: ").append(ZonedDateTime.now().format(HTTP_DATE_FORMAT)).append('\n');

        if (!headers.containsKey("Connection")) {
            sb.append("Connection: keep-alive").append('\n');
        }
        if (contentType != null) {
            sb.append("Content-Type: ").append(contentType).append('\n');
        }

        if (contentLength >= 0) {
            sb.append("Content-Length: ").append(contentLength).append('\n');
        } else {
            sb.append("Transfer-Encoding: chunked").append('\n');
        }

        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            sb.append(header.getKey()).append(": ");
            String comma = "";
            for (String val : header.getValue()) {
                sb.append(comma).append(val);
                comma = ", ";
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public void sendError(int code, String msg) {

        if (isCommitted()) {
            throw new IllegalStateException();
        }

        isCommitted = true;
        errorMessage = msg;
        statusCode = code;
        setContentLength(0);
        try {
            socketOut.write(generateStatusAndHeaders().getBytes());
            socketOut.write("\n".getBytes());
//            socketOut.flush();
        } catch (IOException e) {
            log.error("Failed to commit sendError response", e);
            return;
        }

        log.error("Commit response:\n" + generateStatusAndHeaders());
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

        setStatus(SC_FOUND);
//        setErrorMessage("Found");
        resetBuffer();
        addHeader("Location", encodeURL(s));
        flushBuffer();
        isCommitted = true;
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
