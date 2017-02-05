package edu.upenn.cis.cis455.webserver.connector;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author rtv
 */
public interface RequestProcessor {
    void process(Socket connection) throws IllegalStateException;
    void setServerSocket(ServerSocket serverSocket);
    boolean isAcceptingConnections();
}