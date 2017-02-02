package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpRequestRunnable implements Runnable {

    private static Logger log = LogManager.getLogger(HttpRequestRunnable.class);

    private Socket connection;
    private Container container;

    /**
     * Carries a connection to a client with a request
     * @param connection socket to client
     */
    public HttpRequestRunnable(Socket connection, Container container) {
        this.container = container;
        this.connection = connection;
    }

    /**
     * Tells http to handle the request and closes the socket once the request is served
     */
    @Override
    public void run() {
        try {
            InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream();
            container.serve(in, out);
        } catch (IllegalStateException e) {
            log.error("Invalid Request Ignored", e);
        } catch (IOException e) {
            log.error(e);
        }

        try {
            connection.close();
            log.info("Socket Closed");
        } catch (IOException e) {
            log.error("Could Not Close Socket After Sending Response", e);
        }
    }

}


