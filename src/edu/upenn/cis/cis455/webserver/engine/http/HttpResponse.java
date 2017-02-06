package edu.upenn.cis.cis455.webserver.engine.http;


import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.engine.io.ChunkedOutputStream;
import edu.upenn.cis.cis455.webserver.engine.io.ChunkedPrintWriter;

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
    private ChunkedOutputStream outputStream;

    private Map<String, String> headers = new HashMap<>();

    private ChunkedPrintWriter writer;

    public HttpResponse() {
        date = getHttpDate();
    }


    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setOutputStream(ChunkedOutputStream os) {
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

    public ChunkedPrintWriter getWriter() {
        if (writer == null) {
            writer = new ChunkedPrintWriter(outputStream);
//            writer = new PrintWriter(outputStream);
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
        }


        sb.append("Connection: Keep-Alive").append('\n');

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

}
