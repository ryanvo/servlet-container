package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ConnectionHandler {

    private static Logger log = LogManager.getLogger(ConnectionHandler.class);

    final private ConnectionManager connectionManager;
    final private Container container;

    public ConnectionHandler(ConnectionManager connectionManager, Container container) {
        this.connectionManager = connectionManager;
        this.container = container;
    }

    public void start(int port) throws IOException {

        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(1000);

        log.info(String.format("HTTP ConnectionHandler Started on Port %d", port));

        while (connectionManager.isAcceptingConnections()) {

            Socket connection = null;
            try {
                connection = socket.accept();
            } catch (SocketTimeoutException e) {

                if (connectionManager.isAcceptingConnections()) {
                    continue;
                } else {
                    log.info("Shutdown signal received");
                    break;
                }

            } catch (SocketException e) {
                log.info("ServerSocket Closed Due To Shutdown Request");
            } catch (IOException e) {
                log.error("Unable to Open Socket");
                continue;
            }


            try {
                connectionManager.assign(new HttpRequestRunnable(connection, container));
            } catch (IllegalStateException e) {
                log.info("connectionManager must be off");
                break;
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            log.error("Failed to close ServerSocket");
        }

    }

}