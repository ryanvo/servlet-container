package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Listens for new requests on the specified port. When it receives a client connection
 * it creates a ConnectionHandler and passes it off to the ConnectionManager
 *
 * @rtv
 *
 */
public class HttpRequestListener implements SocketListener {

    private static Logger log = LogManager.getLogger(HttpRequestListener.class);

    final private ConnectionManager connectionManager;
    final private Container container;
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;

    public HttpRequestListener(ConnectionManager connectionManager,
                               Container container,
                               RequestProcessor requestProcessor,
                               ResponseProcessor responseProcessor) {
        this.connectionManager = connectionManager;
        this.container = container;
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
    }

    public void start(int port) throws IOException {

        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(1000);

        while (connectionManager.isRunning()) {

            Socket connection = null;
            try {

                connection = socket.accept();
                log.debug("Connection received");

            } catch (SocketTimeoutException e) {

                if (connectionManager.isRunning()) {
                    continue;
                } else {
                    log.info("Shutdown signal received");
                    break;
                }

            } catch (SocketException e) {
                log.error("Socket error", e);
            } catch (IOException e) {
                log.error("Unable to Open Socket");
                continue;
            }


            try {
                connectionManager.assign(new ConnectionHandler(connection, container, requestProcessor, responseProcessor));
            } catch (IllegalStateException e) {
                log.error("ConnectionManager not accepting connections", e);
                break;
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            log.error("Failed to close ServerSocket", e);
        }

    }

    public ProcessManager getManager() {
        return connectionManager;
    }

}