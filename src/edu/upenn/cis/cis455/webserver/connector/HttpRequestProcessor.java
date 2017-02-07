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



        log.info("Parsed HTTP Request: " + line);
    }

    public Map<String, String> parseHeaders(List<String> headerLines) throws BadRequestException {

        Map<String, String> headers = new HashMap<>();

        for (String line : headerLines) {
            String[] header = line.split(":");

            if (header.length != 2) {
                throw new BadRequestException();
            }

            headers.put(header[0], header[1]);
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
