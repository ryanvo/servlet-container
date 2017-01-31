package edu.upenn.cis.cis455.webserver.thread;

import edu.upenn.cis.cis455.webserver.servlet.ServletContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class HttpRequestRunnable implements Runnable {

    private static Logger log = LogManager.getLogger(HttpRequestRunnable.class);

    private Socket connection;
    private ServletContainer container;

    /**
     * Carries a connection to a client with a request
     * @param connection socket to client
     */
    public HttpRequestRunnable(Socket connection, ServletContainer container) {
        this.container = container;
        this.connection = connection;
    }

    /**
     * Tells servlet to handle the request and closes the socket once the request is served
     */
    @Override
    public void run() {
        try {
            container.dispatch(connection);
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


