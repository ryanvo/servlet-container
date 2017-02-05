package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestRunnable;
import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import org.junit.Test;

import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class HttpRequestProcessorTest {

    @Test
    public void shouldCreateRunnableAndGiveToWorkerPoolAfterReceivingNewConnection() throws Exception {

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        Container mockContainer = mock(Container.class);
        Socket connection = mock(Socket.class);
        when(mockConnectionManager.isAcceptingConnections()).thenReturn(true);

        HttpRequestProcessor processor = new HttpRequestProcessor(mockConnectionManager, mockContainer);
        processor.process(connection);

        verify(mockConnectionManager).assign(isA(HttpRequestRunnable.class));
    }

}
