package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.servlet.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.servlet.http.HttpResponse;
import edu.upenn.cis455.webserver.servlet.io.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Proceses a request before handling it to the container
 *
 * @author rtv
 */
public class HttpResponseProcessor implements ResponseProcessor {


    private static Logger log = LogManager.getLogger(HttpResponseProcessor.class);
    private final static DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    @Override
    public void process(HttpResponse resp, OutputStream socket) throws IOException {


        Buffer buffer = resp.getMsgBodyBuffer();

        if (!resp.getHTTP().endsWith("1")) {
            resp.setContentLength(buffer.size());
        }


        /* Write status line, headers, and CRLF */
        String statusAndHeaders = generateStatusAndHeaders(resp);
        socket.write(statusAndHeaders.getBytes());
        socket.write("\n".getBytes());
        socket.flush();

        log.info("Just wrote:\n" + statusAndHeaders);

        buffer.writeTo(socket);
        socket.flush();
    }

    private String generateStatusAndHeaders(HttpResponse resp) {
        StringBuilder sb = new StringBuilder();

        sb.append(resp.getHTTP()).append(" ").append(resp.getStatusCode()).append(" ").append(resp.getErrorMessage()).append('\n');

        sb.append("Date: ").append(ZonedDateTime.now().format(HTTP_DATE_FORMAT)).append('\n');

        if (resp.getContentType() != null) {
            sb.append("Content-Type: ").append(resp.getContentType()).append('\n');
        }

        if (resp.getContentLength() >= 0) {
            sb.append("Content-Length: ").append(resp.getContentLength()).append('\n');
        } else {
            sb.append("Transfer-Encoding: chunked").append('\n');
        }

        for (Map.Entry<String, List<String>> header : resp.getHeaders().entrySet()) {
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


}
