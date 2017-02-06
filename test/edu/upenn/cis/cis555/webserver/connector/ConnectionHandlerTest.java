package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestRunnable;
import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis555.webserver.HttpTestHelper;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class ConnectionHandlerTest {

    @Test
    public void shouldAcceptConnectionFromSocketAndPassSocketToRequestProcessor() throws Exception {

        final int port = 10542;
        final String host = "localhost";
        final String path = "/";

        Container mockContainer = mock(Container.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.isAcceptingConnections()).thenReturn(true);

        Runnable r1 = () -> {
            try {
                ConnectionHandler handler = new ConnectionHandler(mockConnectionManager, mockContainer);
                handler.start(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread connectionHandlerThread = new Thread(r1);

        Thread sendGetThread = HttpTestHelper.sendGet(host, path, port);

        connectionHandlerThread.start();
        sendGetThread.start();
        sleep(2000);

        verify(mockConnectionManager).assign(isA(HttpRequestRunnable.class));
    }

}
