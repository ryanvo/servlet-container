package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import edu.upenn.cis.cis455.webserver.connector.RequestProcessor;
import edu.upenn.cis.cis555.webserver.HttpTestHelper;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class ConnectionHandlerTest {

    @Test
    public void shouldAcceptConnectionFromSocketAndPassSocketToRequestProcessor() throws Exception {

        final int port = 8081;
        final String host = "localhost";
        final String path = "/";

        RequestProcessor mockRequestProcessor = mock(RequestProcessor.class);

        Runnable r1 = () -> {
            try {
                ConnectionHandler handler = new ConnectionHandler(mockRequestProcessor);
                handler.start(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread connectionHandlerThread = new Thread(r1);
        connectionHandlerThread.start();


        Thread sendGetThread = HttpTestHelper.sendGet(host, path, port);
        sendGetThread.start();
        sleep(1000);

        verify(mockRequestProcessor).process(isA(Socket.class));
    }

    @Test
    public void shouldSetTheServerSocketInRequestProcessor() throws Exception {

        final int port = 8301;

        RequestProcessor mockRequestProcessor = mock(RequestProcessor.class);

        ConnectionHandler connectionHandler = new ConnectionHandler(mockRequestProcessor);

        connectionHandler.start(port);

        verify(mockRequestProcessor).setServerSocket(isA(ServerSocket.class));


    }


}
