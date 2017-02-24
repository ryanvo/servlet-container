package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis455.webserver.engine.http.io.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 *
 * Writes the HTTP response object to the client
 *
 * @author rtv
 */
public class HttpResponseProcessor implements ResponseProcessor {

    private static Logger log = LogManager.getLogger(HttpResponseProcessor.class);
    private final static DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    @Override
    public void process(HttpResponse resp, OutputStream socket) throws IOException {

        boolean isHttp1dot1 = resp.getProtocol().endsWith("1");

        if (resp.getBuffer() == null) {

            resp.setContentLength(0);
            writeStatusAndHeaders(resp, socket);
            socket.flush();
            return;
        }

        /* Set Content-length for HTTP/1.0 */
        Buffer buffer = resp.getMsgBodyBuffer();
        int size = buffer.size();
        if (!isHttp1dot1) {
            resp.setContentLength(size);
        }

        /* Set persistent connection for HTTP/1.1 */
        String connectionHeaderValue = isHttp1dot1 ? "keep-alive" : "close";
        resp.addHeader("Connection", connectionHeaderValue);

        /* Write status line, headers, and CRLF to socket */
        writeStatusAndHeaders(resp, socket);

        /* Write message body buffer to socket */
        buffer.writeTo(socket);
        socket.flush();
        log.debug("Wrote to socket: size:" + size);
    }

    /**
     * Helper method to generate status and headers and write them to the stream
     * @param resp
     * @param out
     * @throws IOException
     */
    public void writeStatusAndHeaders(HttpResponse resp, OutputStream out) throws IOException {

        String statusAndHeaders = generateStatusAndHeaders(resp);
        out.write(statusAndHeaders.getBytes());
        out.write("\r\n".getBytes());
        out.flush();
        log.debug("Sending to socket:\n" + statusAndHeaders);

    }


    /**
     * Helper method to format the HTTP headers appropriately
     * @param resp
     * @return
     */
    public String generateStatusAndHeaders(HttpResponse resp) {
        StringBuilder sb = new StringBuilder();

        sb.append(resp.getProtocol()).append(" ");
        sb.append(resp.getStatusCode()).append(" ");
        sb.append(resp.getErrorMessage()).append('\n');
        sb.append("Date: ").append(ZonedDateTime.now().format(HTTP_DATE_FORMAT)).append('\n');

        /* Include content type if it is set */
        if (resp.getContentType() != null) {
            sb.append("Content-Type: ").append(resp.getContentType()).append('\n');
        }

        /* Include content-length header if it set in response, otherwise set as chunked */
        if (resp.getContentLength() >= 0) {
            sb.append("Content-Length: ").append(resp.getContentLength()).append('\n');
        } else {
            sb.append("Transfer-Encoding: chunked").append('\n');
        }

        /* Write headers */
        for (Map.Entry<String, List<String>> header : resp.getHeaders().entrySet()) {
            sb.append(header.getKey()).append(": ");
            String comma = "";
            for (String val : header.getValue()) {
                sb.append(comma).append(val);
                comma = ", ";
            }
            sb.append("\n");
        }

        for (Cookie cookie : resp.getCookies()) {
            sb.append("Set-Cookie: ");
            sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");

            if (cookie.getMaxAge() > -1) {
                sb.append("Expires=");
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
                ZonedDateTime cookieExpiry = now.plusSeconds(cookie.getMaxAge());
                String cookieExpiryStr = cookieExpiry.format(HTTP_DATE_FORMAT);
                sb.append(cookieExpiryStr);

            }

            log.info(String.format("Cookie set: name:%s val:%s", cookie.getName(), cookie.getValue()));

            sb.append("\n");
        }

        return sb.toString();
    }


}
