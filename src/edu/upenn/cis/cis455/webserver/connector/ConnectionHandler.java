package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler {

    private static Logger log = LogManager.getLogger(ConnectionHandler.class);

    final private HttpRequestProcessor exec;
    private Container container;

    public ConnectionHandler(HttpRequestProcessor exec) {
        this.exec = exec;
        this.container = container;
    }

    /**
     * Loops to accept new connections and tells the executor to schedule them. The server stops
     * when the ServerSocket is closed.
     * @param port
     */
    public void start(int port) {

        try {
            ServerSocket socket = new ServerSocket(port);
//            container.setServerSocket(socket); //TODO put this in engine?
            log.info(String.format("HTTP ConnectionHandler Started on Port %d", port));
            while (exec.isRunning()) {
                try {
                    final Socket connection = socket.accept();
                    exec.process(connection);
                } catch (IllegalStateException e) {
                    log.error("Socket Created Between Client But Executor is Stopped");
                }
            }
        } catch (SocketException e) {
            log.info("ServerSocket Closed Due To Shutdown Request Or Unable to Open Socket");
        } catch (IOException e) {
            log.error("HTTP ConnectionHandler Could Not Open Port " + port, e);
        }

        log.info("ConnectionHandler Successfully Shutdown");
    }

}