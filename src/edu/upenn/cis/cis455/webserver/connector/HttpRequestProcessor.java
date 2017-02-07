package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.exception.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author rtv
 */
public class HttpRequestProcessor implements RequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    @Override
    public void process(HttpRequest request) throws IOException, BadRequestException {
        BufferedReader in = request.getReader();

        String line = in.readLine();

        if (line == null) {
            throw new BadRequestException();
        }

        String[] statusLine = line.split(" ");
        if (statusLine.length != 3) {
            throw new BadRequestException();
        }

        request.setMethod(statusLine[0]);
        request.setUri(statusLine[1]);
        request.setProtocol(statusLine[2]);

        if (!request.getProtocol().matches("HTTP/\\d.\\d")) {
            throw new BadRequestException();
        }

        log.info("Parsed HTTP Request: " + line);


        //TODO set session, parse query arguments, other req fields

    }
}
