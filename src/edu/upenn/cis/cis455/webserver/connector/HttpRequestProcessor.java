package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.exception.InvalidRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author rtv
 */
public class HttpRequestProcessor implements RequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    @Override
    public void process(HttpRequest request) throws IOException, InvalidRequestException {
        BufferedReader in = request.getReader();

        String line = in.readLine();

        if (line == null) {
            throw new InvalidRequestException();
        }

        String[] statusLine = line.split(" ");
        if (statusLine.length != 3) {
            throw new InvalidRequestException();
        }

        request.setMethod(statusLine[0]);
        request.setUri(statusLine[1]);
        request.setProtocol(statusLine[2]);

        if (!request.getProtocol().matches("HTTP/\\d.\\d")) {
            throw new InvalidRequestException();
        }

        log.info("Parsed HTTP Request: " + line);


        //TODO set session, parse query arguments, other req fields

    }
}
