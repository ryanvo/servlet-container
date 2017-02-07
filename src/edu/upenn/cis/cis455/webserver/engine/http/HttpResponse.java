package edu.upenn.cis.cis455.webserver.engine.http;


import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponse {

    private String version;
    private String statusCode;
    private String errorMessage;
    private String date;
    private String contentType;
    private int contentLength = -1;
    private OutputStream outputStream;

    private Map<String, String> headers = new HashMap<>();

    private PrintWriter writer;

    public HttpResponse() {
        date = getHttpDate();
    }


    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    public void setOutputStream(OutputStream os) {
        outputStream = os;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(outputStream);
        }
        return writer;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getStatusAndHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ").append(statusCode).append(" ").append(errorMessage).append('\n')
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

        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
        }

        return sb.toString();
    }

    private static String getHttpDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }


    public void sendError(int code, String msg) {

        String statusLn = String.format("HTTP/1.1 %d %s", code, msg);
        getWriter().println(statusLn);
        getWriter().println();
        getWriter().flush();

    }

}
