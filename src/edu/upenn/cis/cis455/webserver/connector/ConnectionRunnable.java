package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;

public class ConnectionRunnable implements Runnable {

    private static Logger log = LogManager.getLogger(ConnectionRunnable.class);

    private Socket connection;
    private Container container;
    private RequestProcessor requestProcessor;

    public ConnectionRunnable(Socket connection,
                              Container container,
                              RequestProcessor requestProcessor) {
        this.connection = connection;
        this.container = container;
        this.requestProcessor = requestProcessor;
    }

    /**
     * Origin for all servlet requests. All error handling occurs here.
     */
    @Override
    public void run() {

        ConnectionManager manager = (ConnectionManager) container.getContext().getAttribute("ConnectionManager");

            try {

                HttpRequest request = new HttpRequest();
                HttpResponse response = new HttpResponse();

                request.setInputStream(connection.getInputStream());
                response.setOutputStream(connection.getOutputStream());

                requestProcessor.process(request);

                manager.update(Thread.currentThread().getId(), request.getRequestURI());
                container.dispatch(request, response);

                response.getOutputStream().flush();

            } catch (IllegalStateException e) {
                log.error("Invalid Request Ignored", e);
            } catch (IOException e) {
                log.error(e);
            } catch (URISyntaxException e) {

                //TODO http error code handling here
                log.error("Could not parse uri from status line");
            }

            // TODO log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));

        try {
            connection.close();
            manager.update(Thread.currentThread().getId(), "waiting");
            log.info("Socket Closed");
        } catch (IOException e) {
            log.error("Could Not Close Socket After Sending Response", e);
        }
    }
}


