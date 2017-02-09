package edu.upenn.cis.cis455.webserver.servlet.http;


import edu.upenn.cis.cis455.webserver.servlet.MyHttpResponse;
import org.apache.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponse implements HttpServletResponse {

    static Logger log = Logger.getLogger(MyHttpResponse.class);
    private final static String HTTP_1_1 = "HTTP/1.1";
    private String characterEncoding = "ISO-8859-1";
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

    private OutputStream socketOutputStream;
    private ServletOutputStream responseBuffer;
    private PrintWriter writerBuffer;

    public HttpResponse() {
        date = getHttpDate();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setOutputStream(OutputStream out) {
        socketOutputStream = out;
    }

    @Override
    public void setIntHeader(String s, int i) {
        if (isCommitted()) {
            return;
        }
        ArrayList<Integer> intValues = new ArrayList<>();
        intValues.add(i);
        intHeaders.replace(i, intValues);
    }

    @Override
    public void addIntHeader(String s, int i) {
        if (isCommitted()) {
            return;
        }
        List<Integer> intValues = intHeaders.getOrDefault(s, new ArrayList<>());
        intValues.add(i);
    }

    @Override
    public void setStatus(int i) {
        this.statusCode = i;
    }

    @Override
    public void setStatus(int i, String s) {
        this.statusCode = i;
        this.errorMessage = s;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
        addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
    }
    

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

        // TODO: 2/9/17
        // outputstream
        if (writerBuffer == null) {

            responseBuffer.write(HTTP_1_1.getBytes());



        } else {

            writerBuffer.write(HTTP_1_1);



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
        setErrorMessage(null);
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
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public PrintWriter getWriter() {
        if (writerBuffer == null && responseBuffer == null) {
            writerBuffer = new PrintWriter(new ResponseBufferOutputStream(bufferSize));
        } else if (writerBuffer == null) {
            throw new IllegalStateException();
        }
        return writerBuffer;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (responseBuffer == null && writerBuffer == null) {
            responseBuffer = new ResponseBufferOutputStream(bufferSize);
        } else if (responseBuffer == null) {
            throw new IllegalStateException();
        }
        return responseBuffer;
    }

    public String getStatusAndHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(HTTP_1_1).append(" ").append(statusCode).append(" ").append(errorMessage).append('\n')
                .append("Date: ").append(date).append('\n');

        if (contentType != null) {
            sb.append("Content-Type: ").append(contentType).append('\n');
        }

        if (contentLength > 0) {
            sb.append("Content-Length: ").append(contentLength).append('\n');
            sb.append("Connection: close").append('\n');
        } else {
            sb.append("Connection: keep-alive").append('\n');
        }

        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
        }

        return sb.toString();
    }

    private static String getHttpDate() { // TODO: 2/9/17 depricated to zonedtimedate
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }


    @Override // TODO: 2/9/17 figureout how to do it right
    public void sendError(int code, String msg) {

        String statusLn = String.format("HTTP/1.1 %d %s", code, msg);
        getWriter().println(statusLn);
        getWriter().println();
        getWriter().flush();


//        if (isCommitted()) {
//            throw new IllegalStateException();
//        }
//        setStatus(statusCode);
//        setErrorMessage(msg);
//        setContentType("text/html");
//        getWriter().println(msg);
//        flushBuffer();
    }

    @Override
    public void sendError(int i) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException();
        }
        setStatus(statusCode);
        setErrorMessage(msg);
        setContentType("text/html");
        getWriter().println(msg);
        flushBuffer();
    }

    @Override
    public void sendRedirect(String s) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        setStatus(SC_FOUND);
        setErrorMessage("Found");
        resetBuffer();
        addHeader("Location", encodeURL(s));
        flushBuffer();
        isCommitted = true;
    }

    @Override
    public void setDateHeader(String key, long val) {
        ArrayList<Long> dateValues = new ArrayList<>();
        dateValues.add(val);
        dateHeaders.replace(key, dateValues);
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
        headers.replace(key, headerValues);
    }

    @Override @Deprecated
    public String encodeUrl(String s) {
        return null;
    }

    @Override @Deprecated
    public String encodeRedirectUrl(String s) {
        return null;
    }


}
