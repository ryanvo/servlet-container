package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.*;
import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis555.webserver.HttpTestHelper;
import org.apache.http.protocol.HTTP;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class ConnectionHandlerTest {

    @Test
    public void shouldAcceptConnectionFromSocketAndPassSocketToRequestProcessor() throws Exception {

        final int port = 10542;
        final String host = "localhost";

        ResponseProcessor mockResponseProcessor = mock(ResponseProcessor.class);
        RequestProcessor mockRequestProcessor = mock(RequestProcessor.class);
        Container mockContainer = mock(Container.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        when(mockConnectionManager.isAcceptingConnections()).thenReturn(true);
        doThrow(IllegalStateException.class).when(mockConnectionManager).assign(any(ConnectionRunnable.class));

        Runnable runConnectionHandler = () -> {
            try {
                ConnectionHandler handler = new ConnectionHandler(mockConnectionManager,
                                                                  mockContainer,
                                                                  mockRequestProcessor,
                                                                  mockResponseProcessor);
                handler.start(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };Thread connectionHandlerThread = new Thread(runConnectionHandler);
        connectionHandlerThread.start();
        sleep(2000);

        try (
                Socket echoSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        ) {
            out.println("GET / HTTP/1.1");
            out.close();
        }

        verify(mockConnectionManager, timeout(5000)).assign(isA(ConnectionRunnable.class));
    }

}
