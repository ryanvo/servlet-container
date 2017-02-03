package edu.upenn.cis.cis455.webserver.engine.servlet;


import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HttpResponse {

    private String version;
    private String statusCode;
    private String errorMessage;
    private String date;
    private String contentType;
    private int contentLength;
    private OutputStream outputStream;

    public HttpResponse() {
        date = getHttpDate();
    }

    public HttpResponse reset() {

        return this;
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


    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getStatusAndHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ").append(statusCode).append(" ").append(errorMessage).append('\n')
                .append("Date: ").append(date).append('\n')
                .append("Content-Type: ").append(contentType).append('\n')
                .append("Content-Length: ").append(contentLength).append('\n')
                .append("Connection: Keep-Alive").append('\n');
        return sb.toString();
    }

    private static String getHttpDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

}
