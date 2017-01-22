package edu.upenn.cis.cis455.webserver.servlet.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequest {

    private static Logger log = LogManager.getLogger(HttpRequest.class);

    private String method;
    private URI uri;
    private String type;

    /**
     * Reads the socket and parses the information of the requests
     * @param connection socket to the client
     * @throws URISyntaxException if the uri in the status line of the request is invalid
     */
    public HttpRequest(Socket connection) throws URISyntaxException {
        parseRequest(connection);
    }

    /**
     * @return type of request e.g. GET, POST...
     */
    public String getType() {
        return type;
    }

    /**
     * @return uri requested in status line
     */
    public URI getRequestURI() {
        return uri;
    }

    /**
     * Parses the status line from the socket to initialize class members
     * @param connection to client
     * @throws URISyntaxException is uri data cannot be properly parsed
     */
    private void parseRequest(Socket connection) throws URISyntaxException {

        String line;
        try  {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            line = in.readLine();

        } catch (IOException e) { //TODO throw at constructor
            log.error("Socket IOException", e);
            return;
        }

        log.info("Parsing HTTP Request: " + line);

        String[] statusLine = line.split(" ");
        method = statusLine[0];
        uri = new URI(statusLine[1]);

        if (uri.getPath().matches("/+control/*$")) {
            type = "control";
        } else if (uri.getPath().matches("/+shutdown/*$")) {
            type = "shutdown";
        } else if (method.equals("GET")) {
            type = "get";
        }

        log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));
    }

}
