package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.servlet.http.HttpResponse;
import edu.upenn.cis455.webserver.servlet.io.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
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

        boolean isHttp1dot1 = resp.getProtocol().endsWith("1");

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
        sendStatusAndHeaders(resp, socket);

        /* Write message body buffer to socket */
        buffer.writeTo(socket);
        socket.flush();
        log.info("Wrote buffer to socket: size:" + size);
    }

    public void sendStatusAndHeaders(HttpResponse resp, OutputStream out) throws IOException {

        String statusAndHeaders = generateStatusAndHeaders(resp);
        out.write(statusAndHeaders.getBytes());
        out.write("\r\n".getBytes());
        out.flush();
        log.info("Wrote buffer to socket:\n" + statusAndHeaders);

    }


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
                long cookieDuration = cookie.getMaxAge();
                ZonedDateTime now = ZonedDateTime.now();
                ZonedDateTime cookieExpiry = now.plus(Duration.ofSeconds(cookieDuration));
                sb.append(now.plusSeconds((long) cookie.getMaxAge()).format(HTTP_DATE_FORMAT));
            }
            sb.append("\n");
        }

        return sb.toString();
    }


}
