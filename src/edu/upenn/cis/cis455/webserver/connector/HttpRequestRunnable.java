package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestRunnable implements Runnable {

    private static Logger log = LogManager.getLogger(HttpRequestRunnable.class);

    private Socket connection;
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
            container.dispatch(createRequest(new HttpRequest()), createResponse(new HttpResponse()));
        } catch (IllegalStateException e) {
            log.error("Invalid Request Ignored", e);
        } catch (IOException e) {
            log.error(e);
        } catch (URISyntaxException e) {
            log.error("Could not parse uri from status line");
        }

        // TODO log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));

        try {
            connection.close();
            log.info("Socket Closed");
        } catch (IOException e) {
            log.error("Could Not Close Socket After Sending Response", e);
        }
    }

    public HttpRequest createRequest(HttpRequest req) throws IOException, URISyntaxException {

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = in.readLine();


        String[] statusLine = line.split(" ");
        String method = statusLine[0];
        req.setUri(statusLine[1]);

        log.info("Parsed HTTP Request: " + line);

        req.setType(method);
        //TODO set session, parse query arguments, other req fields
        return req;
    }

    public HttpResponse createResponse(HttpResponse resp) throws IOException {

        resp.setOutputStream(connection.getOutputStream());

        return resp;

    }

}


