package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
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
    public void process(HttpRequest request) throws IOException, URISyntaxException {
        BufferedReader in = request.getReader();

        //TODO null check here
        String line = in.readLine();
        String[] statusLine = line.split(" ");
        String method = statusLine[0];
        request.setUri(statusLine[1]);

        log.info("Parsed HTTP Request: " + line);

        request.setMethod(method);
        //TODO set session, parse query arguments, other req fields

    }
}
