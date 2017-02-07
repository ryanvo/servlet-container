package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.exception.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rtv
 */
public class HttpRequestProcessor implements RequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    @Override
    public void process(HttpRequest request) throws IOException, BadRequestException {
        BufferedReader in = request.getReader();

        String line = in.readLine();

        String[] statusLine = parseStatusLine(line);
        request.setMethod(statusLine[0]);
        request.setUri(statusLine[1]);
        request.setProtocol(statusLine[2]);

        List<String> lines = new ArrayList<>();
        while (line != null) {
            line = in.readLine();
            if (line.equals("\n")) {
                break;
            }
            lines.add(line);
        }
//
//        Map<String, String> headers = parseHeaders(lines);
//        for (Map.Entry<String, String> e : headers) {
//            request.
//        }

        log.info("Parsed HTTP Request: " + line);
    }

    public Map<String, List<String>> parseHeaders(List<String> headerLines) throws BadRequestException {

        Map<String, List<String>> headers = new HashMap<>();


        String prevHeaderKey = "";
        for (String line : headerLines) {
            String currHeaderKey;
            String[] headerEntry, headerValues;


            if (line.startsWith(" ") ||  line.startsWith("\t")) {

                /* Values belong header entry on previous line */
                currHeaderKey = prevHeaderKey;
                headerValues = line.split(",");

            } else {

                headerEntry = line.split(":", 2);
                if (headerEntry.length != 2) {
                    throw new BadRequestException();
                }
                headerValues = headerEntry[1].split(",");
                currHeaderKey = headerEntry[0].trim();
            }

            List<String> headerValuesList = new ArrayList<>();
            for (String val : headerValues) {
                headerValuesList.add(val.trim().toLowerCase());
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

        String[] statusLine = line.split(" ");
        if (statusLine.length != 3) {
            throw new BadRequestException();
        }

        String protocol = statusLine[3];

        if (!protocol.matches("HTTP/\\d.\\d")) {
            throw new BadRequestException();
        }

        return statusLine;
    }
}
