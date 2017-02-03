package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestRunnable implements Runnable {

    private static Logger log = LogManager.getLogger(HttpRequestRunnable.class);

    private Socket connection;
    private HttpRequest request = new HttpRequest();
    private HttpResponse response = new HttpResponse();
    private Container container;


    public HttpRequestRunnable(Socket connection, Container container) {
        this.connection = connection;
        this.container = container;
    }

    /**
     * Tells http to handle the request and closes the socket once the request is served
     */
    @Override
    public void run() {

        //TODO do the request, the response, session if necessary
        try {
            container.dispatch(createRequest(request), response);
        } catch (IllegalStateException e) {
            log.error("Invalid Request Ignored", e);
        } catch (IOException e) {
            log.error(e);
        }
        // TODO log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));

        try {
            connection.close();
            log.info("Socket Closed");
        } catch (IOException e) {
            log.error("Could Not Close Socket After Sending Response", e);
        }
    }

    public HttpRequest createRequest(HttpRequest req) throws IOException {
        req.reset();
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        line = in.readLine();

        log.info("Parsing HTTP Request: " + line);

        String[] statusLine = line.split(" ");
        String method = statusLine[0];

        URI uri = null;
        try {
            uri = new URI(statusLine[1]);
        } catch (URISyntaxException e) {
            log.error(e);
            throw new IOException();
        }

        req.setType(method);
        req.setUri(uri);

        return request;
    }

    public HttpResponse createResponse(HttpResponse resp) {
        resp.reset();

        return response;

    }

}


