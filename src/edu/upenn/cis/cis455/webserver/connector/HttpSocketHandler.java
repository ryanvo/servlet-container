package edu.upenn.cis.cis455.webserver.connector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class HttpSocketHandler {

    private static Logger log = LogManager.getLogger(HttpSocketHandler.class);

    final private HttpRequestProcessor exec;

    public HttpSocketHandler(HttpRequestProcessor exec) {
        this.exec = exec;
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
            log.info(String.format("HTTP HttpSocketHandler Started on Port %d", port));
            while (exec.isRunning()) {
                try {
                    Socket connection = socket.accept();
                    exec.process(connection);
                } catch (IllegalStateException e) {
                    log.error("Socket Created Between Client But Executor is Stopped");
                }
            }
        } catch (SocketException e) {
            log.info("ServerSocket Closed Due To Shutdown Request Or Unable to Open Socket");
        } catch (IOException e) {
            log.error("HTTP HttpSocketHandler Could Not Open Port " + port, e);
        }

        log.info("HttpSocketHandler Successfully Shutdown");
    }

}