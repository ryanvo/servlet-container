package edu.upenn.cis.cis555.webserver.engine;

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
public class WebContainerTest {

    @Test
    public void shouldCreateRunnableAndGiveToWorkerPoolAfterReceivingNewConnection() throws Exception {

//        WorkerPool mockWorkerPool = mock(WorkerPool.class);
//        Container mockContainer = mock(Container.class);
//        Socket connection = mock(Socket.class);
//
//        HttpRequestProcessor processor = new HttpRequestProcessor(mockWorkerPool, mockContainer);
//        processor.process(connection);
//
//        verify(mockWorkerPool).assign(isA(HttpRequestRunnable.class));
    }

}
