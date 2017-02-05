package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis.cis455.webserver.thread.WorkerThread;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class ConnectionManagerTest {

    @Test
    public void shouldInitializeThreadStatusToWaiting() throws Exception {
        final long workerId = 12L;

        WorkerPool mockWorkerPool = mock(WorkerPool.class);

        Set<WorkerThread> mockSetOfWorkers = new HashSet<>();
        WorkerThread mockWorkerThread = mock(WorkerThread.class);
        mockSetOfWorkers.add(mockWorkerThread);
        when(mockWorkerThread.getId()).thenReturn(workerId);
        when(mockWorkerPool.getWorkers()).thenReturn(mockSetOfWorkers);

        ConnectionManager connectionManager = new ConnectionManager(mockWorkerPool);
        Map<Long, String> status = connectionManager.getStatus();

        assertThat(status.get(workerId), is("waiting"));
        assertThat(status.entrySet(), hasSize(1));
    }

    @Test
    public void shouldUpdateTheCurrentUriOfTheThreadWhenUpdateInvoked() throws Exception {

            final long workerId = 12L;
            final String uri = "/foo/bar";

            WorkerPool mockWorkerPool = mock(WorkerPool.class);

            Set<WorkerThread> mockSetOfWorkers = new HashSet<>();
            WorkerThread mockWorkerThread = mock(WorkerThread.class);
            mockSetOfWorkers.add(mockWorkerThread);
            when(mockWorkerThread.getId()).thenReturn(workerId);
            when(mockWorkerPool.getWorkers()).thenReturn(mockSetOfWorkers);

            ConnectionManager connectionManager = new ConnectionManager(mockWorkerPool);
            connectionManager.update(workerId, uri);
            Map<Long, String> status = connectionManager.getStatus();

            assertThat(status.get(workerId), is(uri));
            assertThat(status.entrySet(), hasSize(1));

    }

    @Test
    public void shouldOfferRunnableToWorkerPool() throws Exception {

        WorkerPool mockWorkerPool = mock(WorkerPool.class);
        Runnable mockRunnable = mock(Runnable.class);

        ConnectionManager connectionManager = new ConnectionManager(mockWorkerPool);
        connectionManager.assign(mockRunnable);

        verify(mockWorkerPool).offer(mockRunnable);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldStopAcceptingConnectionsUponShutdown() {

        WorkerPool mockWorkerPool = mock(WorkerPool.class);
        Runnable mockRunnable = mock(Runnable.class);

        ConnectionManager connectionManager = new ConnectionManager(mockWorkerPool);
        connectionManager.shutdown();

        assertThat(connectionManager.isAcceptingConnections(), is(false));
        connectionManager.assign(mockRunnable);
    }

    @Test
    public void shouldKillWorkerPoolUponShutdown() {

        WorkerPool mockWorkerPool = mock(WorkerPool.class);
        Runnable mockRunnable = mock(Runnable.class);

        ConnectionManager connectionManager = new ConnectionManager(mockWorkerPool);
        connectionManager.shutdown();

        verify(mockWorkerPool).kill();
    }

}
