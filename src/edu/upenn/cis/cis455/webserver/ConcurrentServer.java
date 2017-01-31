package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.thread.HttpRequestRunnable;
import edu.upenn.cis.cis455.webserver.servlet.DefaultServlet;
import edu.upenn.cis.cis455.webserver.thread.WorkExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ConcurrentServer {

    private static Logger log = LogManager.getLogger(ConcurrentServer.class);

    final private WorkExecutorService exec;
    final private DefaultServlet servlet;

    public ConcurrentServer(WorkExecutorService exec, DefaultServlet servlet) {
        this.exec = exec;
        this.servlet = servlet;
    }

    /**
     * Loops to accept new connections and tells the executor to schedule them. The server stops
     * when the ServerSocket is closed.
     * @param port
     */
    public void start(int port) {

        try {
            ServerSocket socket = new ServerSocket(port);
            servlet.setServerSocket(socket);
            log.info(String.format("HTTP Server Started on Port %d", port));
            while (exec.isRunning()) {
                try {
                    Socket connection = socket.accept();
                    exec.execute(new HttpRequestRunnable(connection, servlet));
                } catch (IllegalStateException e) {
                    log.error("Socket Created Between Client But Executor is Stopped");
                }
            }
        } catch (SocketException e) {
            log.info("ServerSocket Closed Due To Shutdown Request Or Unable to Open Socket");
        } catch (IOException e) {
            log.error("HTTP Server Could Not Open Port " + port, e);
        }

        log.info("Server Successfully Shutdown");
    }

}