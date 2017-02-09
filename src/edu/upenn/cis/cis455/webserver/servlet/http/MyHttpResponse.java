//package edu.upenn.cis.cis455.webserver.servlet;
//
//
//import org.apache.log4j.Logger;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.Cookie;
//import java.io.*;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class MyHttpResponse implements javax.servlet.http.HttpServletResponse {
//
//    static Logger log = Logger.getLogger(MyHttpResponse.class);
//
//    private String version = "HTTP/1.1";
//    private int statusCode = -1;
//    private String errorMessage;
//    private String contentType;
//
//
//    private boolean isCommitted = false;
//
//    private ArrayList<Cookie> cookies = new ArrayList<>();
//    private HashMap<String, ArrayList<Long>> dateHeaders = new HashMap<>();
//    private HashMap<String, ArrayList<String>> headers = new HashMap<>();
//    private HashMap<String, ArrayList<Integer>> intHeaders = new HashMap<>();
//
//    private int bufferSize;
//    private PrintWriter writer = null;
//    private OutputStream outputStream;
//
//    public MyHttpResponse() {
//        ArrayList<String> connectionHeader =  new ArrayList<>();
//        connectionHeader.add("Keep-Alive");
//        headers.replace("Connection", connectionHeader);
//    }
//
//
//    public void setStatus(int status) {
//        if (isCommitted()) {
//            return;
//        }
//        this.statusCode = status;
//    }
//
//    @Override
//    public int getBufferSize() {
//        return bufferSize;
//    }
//
//    @Override
//    public void setBufferSize(int size) {
//        if (isCommitted()) {
//            return;
//        }
//        this.bufferSize = size;
//    }
//
//    @Override
//    public void flushBuffer() throws IOException {
//        getWriter().flush();
//    }
//
//    @Override
//    public void resetBuffer() {
//        if (isCommitted()) {
//            throw new IllegalStateException();
//        }
//
//        try {
//            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream,
//                    getCharacterEncoding()), getBufferSize()), false);
//        } catch (UnsupportedEncodingException e) {
//            log.error("Unsupported Character Encoding (shouldn't happen)", e);
//        }
//    }
//
//    @Override
//    public boolean isCommitted() {
//        return isCommitted;
//    }
//
//
//    public void reset() {
//        if (isCommitted()) {
//            throw new IllegalStateException();
//        }
//        resetBuffer();
//        setStatus(-1);
//        setErrorMessage(null);
//        dateHeaders.clear();
//        headers.clear();
//        intHeaders.clear();
//    }
//
//
//
////
////    @Override
////    public Locale getLocale() {
////        return locale;
////    }
////
////    @Override
////    public void setLocale(Locale locale) {
////        if (isCommitted()) {
////            return;
////        }
////        this.locale = locale;
////    }
////
////    @Override
////    public String getCharacterEncoding() {
////        return characterEncoding;
////    }
////
////    @Override
////    public void setCharacterEncoding(String s) {
////        if (isCommitted()) {
////            return;
////        }
////        this.characterEncoding = s;
////    }
//
////    @Override
////    public String getContentType() {
////        return contentType;
////    }
//
//    public void setContentType(String type) {
//        if (isCommitted()) {
//            return;
//        }
//        this.contentType = type;
//        ArrayList<String> contentType = new ArrayList<>();
//        contentType.add(type);
//        headers.replace("Content-Type", contentType);
//    }
//
//    @Override /* server only supports getWriter() */
//    public ServletOutputStream getOutputStream() throws IOException {
//        return null;
//    }
//
//    public void setOutputStream(OutputStream os) {
//        if (isCommitted()) {
//            return;
//        }
//        this.outputStream = os;
//    }
//
//    @Override
//    public PrintWriter getWriter() throws IOException {
//        if (writer == null) {
//            /* create new PrintWriter and append status and header to buffer */
//            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream,
//                    getCharacterEncoding()), getBufferSize()), false);
//            setDateHeader("Date", System.currentTimeMillis() / 1000);
//            writer.println(getStatusAndHeader());
//            flushBuffer();
//            isCommitted = true;
//        }
//        return writer;
//    }
//
//    @Override
//    public void setContentLength(int length) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<Integer> contentLength =  new ArrayList<>();
//        contentLength.add(length);
//        intHeaders.replace("Content-Length", contentLength);
//    }
//
//    @Override
//    public void addCookie(Cookie cookie) {
//        if (isCommitted()) {
//            return;
//        }
//        cookies.add(cookie);
//        addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
//    }
//
//    @Override
//    public boolean containsHeader(String s) {
//        return dateHeaders.containsKey(s) || headers.containsKey(s) || intHeaders.containsKey(s);
//    }
//
//    @Override /* server specs assume cookie support */
//    public String encodeURL(String s) {
//        return url;
//    }
//
//    @Override /* server specs assume cookie support */
//    public String encodeRedirectURL(String s) {
//        return url;
//    }
//
//    @Override @Deprecated
//    public String encodeUrl(String s) {
//        return null;
//    }
//
//    @Override @Deprecated
//    public String encodeRedirectUrl(String s) {
//        return null;
//    }
//
//    @Override
//    public void sendError(int statusCode, String msg) throws IOException {
//        if (isCommitted()) {
//            throw new IllegalStateException();
//        }
//        setStatus(statusCode);
//        setErrorMessage(msg);
//        setContentType("text/html");
//        getWriter().println(msg);
//        flushBuffer();
//    }
//
//    @Override
//    public void sendError(int statusCode) throws IOException {
//        if (isCommitted()) {
//            throw new IllegalStateException();
//        }
//        setStatus(statusCode);
//        setErrorMessage("Error");
//        setContentType("text/html");
//        getWriter();
//        flushBuffer();
//    }
//
//    @Override
//    public void sendRedirect(String location) throws IOException {
//        if (isCommitted()) {
//            throw new IllegalStateException();
//        }
//
//        setStatus(SC_FOUND);
//        setErrorMessage("Found");
//        resetBuffer();
//        addHeader("Location", encodeURL(location));
//        flushBuffer();
//        isCommitted = true;
//    }
//
//    @Override
//    public void setDateHeader(String name, long value) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<Long> dateValues = new ArrayList<>();
//        dateValues.add(value);
//        dateHeaders.replace(name, dateValues);
//    }
//
//    @Override
//    public void addDateHeader(String s, long l) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<Long> dateValues = dateHeaders.getOrDefault(s, new ArrayList<>());
//        dateValues.add(l);
//    }
//
//    @Override
//    public void setHeader(String name, String value) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<String> headerValues = new ArrayList<>();
//        headerValues.add(value);
//        headers.replace(name, headerValues);
//    }
//
//    @Override
//    public void addHeader(String name, String value) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<String> headerValues = headers.getOrDefault(name, new ArrayList<>());
//        headerValues.add(value);
//    }
//
//    @Override
//    public void setIntHeader(String name, int value) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<Integer> intValues = new ArrayList<>();
//        intValues.add(value);
//        intHeaders.replace(name, intValues);
//    }
//
//    @Override
//    public void addIntHeader(String name, int value) {
//        if (isCommitted()) {
//            return;
//        }
//        ArrayList<Integer> intValues = intHeaders.getOrDefault(name, new ArrayList<>());
//        intValues.add(value);
//    }
//
//    @Override
//    @Deprecated
//    public void setStatus(int i, String s) {
//
//    }
//
//    public void setVersion(String version) {
//        if (isCommitted()) {
//            return;
//        }
//        this.version = version;
//    }
//
//    public void setErrorMessage(String errorMessage) {
//        if (isCommitted()) {
//            return;
//        }
//        this.errorMessage = errorMessage;
//    }
//
//    public void setURL(String url) {
//        if (isCommitted()) {
//            return;
//        }
//        this.url = url;
//    }
//
//    private String getStatusAndHeader() {
//        StringBuilder sb = new StringBuilder();
//
//        /* Status */
//        sb.append(version).append(" ").append(statusCode).append(" ").append(errorMessage).append
//                ('\n');
//
//        /* Date headers */
//        for (Map.Entry<String, ArrayList<Long>> date : dateHeaders.entrySet()) {
//            sb.append(date.getKey()).append(": ");
//            for (long unixTime : date.getValue()) {
//                Date unixTimeAsDate = new Date(unixTime * 1000);
//                sb.append(unixToHttpDate(unixTimeAsDate)).append(',');
//            }
//        }
//        sb.setLength(sb.length() - 1); /* remove trailing comma */
//        sb.append('\n');
//
//        /* Headers */
//        for (Map.Entry<String, ArrayList<String>> header : headers.entrySet()) {
//            sb.append(header.getKey()).append(": ");
//            for (String value : header.getValue()) {
//                sb.append(value).append(',');
//            }
//        }
//        sb.setLength(sb.length() - 1); /* remove trailing comma */
//        sb.append('\n');
//
//        /* Int headers */
//        for (Map.Entry<String, ArrayList<Integer>> intHeader : intHeaders.entrySet()) {
//            sb.append(intHeader.getKey()).append(": ");
//            for (int value : intHeader.getValue()) {
//                sb.append(value).append(',');
//            }
//        }
//        sb.setLength(sb.length() - 1); /* remove trailing comma */
//        sb.append('\n');
//
//        return sb.toString();
//    }
//
//    private String unixToHttpDate(Date unixTime) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", getLocale());
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return dateFormat.format(unixTime);
//    }
//}