package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.http.exception.file.IllegalFilePathException;
import edu.upenn.cis455.webserver.http.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.http.HttpRequest;
import edu.upenn.cis455.webserver.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proceses a request before handling it to the container
 * @author rtv
 */
public class HttpRequestProcessor implements RequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    /**
     * Reads the input stream of an HTTP request and populates an HttpRequest object
     * @param request empty HttpRequest object
     * @throws IOException error reading socket
     * @throws BadRequestException illegal request format
     */
    @Override
    public void process(HttpRequest request) throws IOException, BadRequestException {
        BufferedReader in = request.getReader();
        String line = in.readLine();

        log.info("Incoming request: " + line);

        /* Process status line */
        String[] statusLine = parseStatusLine(line);
        request.setMethod(statusLine[0]);
        request.setUri(statusLine[1]);
        request.setProtocol(statusLine[2]);

        /* Read in headers from request */
        List<String> lines = new ArrayList<>();
        for (line = in.readLine(); line != null && !line.isEmpty(); line = in.readLine()) {
            lines.add(line);
            log.debug("Header line: " + line);
        }

        /* Process headers */
        Map<String, List<String>> headers = parseHeaders(lines);
        request.setHeaders(headers);


        /* Check that Host header exists for HTTP/1.1 and up */
        if (!hasValidHostHeader(request.getProtocol(), request.getHeaders())) {
            log.info("Request is missing Host header entry");
            throw new BadRequestException();
        }


        String cookiesField = request.getHeader("cookie");
        if (cookiesField != null) {
            request.setCookies(parseCookies(cookiesField));
        }

        log.info("Processed HTTP Request: status:" + line);
    }

    public List<Cookie> parseCookies(String cookieField) throws BadRequestException {
        List<Cookie> cookies = new ArrayList<>();

        String[] cookiesSplit = cookieField.split(";");
        for (String cookie : cookiesSplit) {

            cookie = cookie.trim();
            String[] cookieEntry = cookie.split("=");
            if (cookieEntry.length < 2) {
                throw new BadRequestException();
            }

            cookies.add(new Cookie(cookieEntry[0], cookieEntry[1]));
        }

        return cookies;
    }

    /**
     * Creates a map of the headers given in a response method. Handles multiline headers.
     * @param headerLines part of response after status line and before CRLF
     * @return map of header field to values
     * @throws BadRequestException illegal headers in the request
     */
    public Map<String, List<String>> parseHeaders(List<String> headerLines) throws BadRequestException {

        Map<String, List<String>> headers = new HashMap<>();

        String prevHeaderKey = "";
        for (String line : headerLines) {
            String currHeaderKey;
            String[] headerEntry, headerValues;

            if (line.startsWith(" ") ||  line.startsWith("\t")) {

                /* Values belong to header entry on previous line */
                currHeaderKey = prevHeaderKey;
                headerValues = line.split(",");

            } else {

                headerEntry = line.split(":", 2);
                if (headerEntry.length != 2) {
                    throw new BadRequestException();
                }

                currHeaderKey = headerEntry[0].trim().toLowerCase();

                if (headerEntry[1].contains("GMT")) { // Apparently date is a single value but has comma
                    headerValues = new String[] {headerEntry[1]};
                } else {
                    headerValues = headerEntry[1].split(",");
                }
            }

            /* Get list of values for the key or create new one */
            List<String> headerValuesList = headers.getOrDefault(currHeaderKey, new ArrayList<>());
            for (String val : headerValues) {
                headerValuesList.add(val.trim());
            }

            headers.put(currHeaderKey, headerValuesList);
            prevHeaderKey = currHeaderKey;
        }

        return headers;
    }

    /**
     * Splits and normalizes HTTP request status line into an array with:
     *  Method @ index 0
     *  Path @ index 1
     *  HTTP version @ index 2
     * @param line status line of HTTP request
     * @return array of normalized components of status line
     * @throws BadRequestException - line contains incorrect number of arguments
     *                             - illegal file path
     *                             - invalid HTTP version
     */
    public String[] parseStatusLine(String line) throws BadRequestException {

        String[] statusLine = line.split("\\s+");
        if (statusLine.length != 3) {
            log.debug("Incorrect number of arguments in status line: " + line);
            throw new BadRequestException();
        }
        String path = statusLine[1];

        /* Handle absolute path case */
        if (path.startsWith("http://")) {
            path = FileUtil.getUrlPath(path);
        }

        /* Normalize all paths */
        try {
            statusLine[1] = FileUtil.normalizePath(path);
        } catch (IllegalFilePathException e) {
            log.debug("Received illegal path: " + statusLine[1]);
            throw new BadRequestException();
        }

        /* Validate legal HTTP version */
        String protocol = statusLine[2];
        if (!protocol.matches("HTTP/\\d.\\d")) {
            log.debug("Invalid HTTP version: " + statusLine[1]);
            throw new BadRequestException();
        }

        log.debug("Parsed status line: " + line);
        return statusLine;
    }

    /* Check that Host header exists for HTTP/1.1 and up */
    public boolean hasValidHostHeader(String version, Map<String, List<String>> headers) {
        return !version.endsWith("1") || headers.containsKey("host");
    }

}
