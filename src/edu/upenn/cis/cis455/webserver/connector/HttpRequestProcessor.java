package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.exception.file.IllegalFilePathException;
import edu.upenn.cis.cis455.webserver.exception.http.BadRequestException;
import edu.upenn.cis.cis455.webserver.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
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

    @Override
    public void process(HttpRequest request) throws IOException, BadRequestException {
        BufferedReader in = request.getReader();
        String line = in.readLine();

        if (line == null) {
            throw new SocketException();
        }

        /* Process status line */
        String[] statusLine = parseStatusLine(line);
        request.setMethod(statusLine[0]);
        request.setUri(statusLine[1]);
        request.setProtocol(statusLine[2]);

        /* Read in headers from request */
        List<String> lines = new ArrayList<>();
        for (line = in.readLine(); !line.equals(null) && !line.isEmpty(); line = in.readLine()) {
            lines.add(line);
            log.debug("Header line: " + line);
        }

        /* Process headers */
        Map<String, List<String>> headers = parseHeaders(lines);
        request.setHeaders(headers);

        log.info("Processed HTTP Request: " + line);
    }

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

    public String[] parseStatusLine(String line) throws BadRequestException {

        if (line == null) {
            throw new BadRequestException();
        }

        String[] statusLine = line.split("\\s+");
        if (statusLine.length != 3) {
            log.debug("Incorrect number of arguments in status line: " + line);
            throw new BadRequestException();
        }

        String path = statusLine[1];
        if (!path.startsWith("http://")) {
            path = FileUtil.getUrlPath(path);
        }

        try {
            statusLine[1] = FileUtil.normalizePath(path);
        } catch (IllegalFilePathException e) {
            log.debug("Received illegal path: " + statusLine[1]);
            throw new BadRequestException();
        }

        String protocol = statusLine[2];
        if (!protocol.matches("HTTP/\\d.\\d")) {
            log.debug("Invalid HTTP version: " + statusLine[1]);
            throw new BadRequestException();
        }

        log.debug("Parsed status line: " + line);
        return statusLine;
    }

}
