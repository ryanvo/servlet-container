package edu.upenn.cis.cis455.webserver.connector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ConnectionHandler {

    private static Logger log = LogManager.getLogger(ConnectionHandler.class);

    final private RequestProcessor requestProcessor;

    public ConnectionHandler(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void start(int port) throws IOException {

        ServerSocket socket = new ServerSocket(port);
        log.info(String.format("HTTP ConnectionHandler Started on Port %d", port));

        socket.setSoTimeout(200);

        while (requestProcessor.isAcceptingConnections()) {

            Socket connection = null;
            try {
                connection = socket.accept();

            } catch (SocketTimeoutException e) {

                log.error("FOR REAL");

            } catch (SocketException e) {
                log.info("ServerSocket Closed Due To Shutdown Request");
            } catch (IOException e) {
                log.error("Unable to Open Socket");
                continue;
            }

            try {
                requestProcessor.process(connection);
            } catch (IllegalStateException e) {
                log.info("RequestProcessor must be off");
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