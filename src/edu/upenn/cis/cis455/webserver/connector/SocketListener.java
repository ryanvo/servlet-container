package edu.upenn.cis.cis455.webserver.connector;

import java.io.IOException;

/**
 * @author rtv
 */
public interface SocketListener {

    void start(int port) throws IOException;

}
