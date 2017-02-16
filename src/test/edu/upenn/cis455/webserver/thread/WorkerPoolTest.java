package edu.upenn.cis455.webserver.thread;

import edu.upenn.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis455.webserver.thread.WorkQueue;
import edu.upenn.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis455.webserver.thread.WorkerThread;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class WorkerPoolTest {

    @Test
    public void shouldCreateCorrectNumberOfThreads() throws Exception {
        final int size = 12;

        WorkQueue mockWorkQueue = mock(WorkQueue.class);
        when(mockWorkQueue.take()).thenReturn(() -> {}); // dummy Runnable w/ empty run()

        WorkerPool workerPool = new WorkerPool(size, mockWorkQueue);

        assertThat(workerPool.getWorkers(), hasSize(size));
    }

    @Test
    public void shouldAddWorkToQueueWhenOfferInvoked() throws Exception {
        final int size = 12;

        WorkQueue mockWorkQueue = mock(WorkQueue.class);
        ConnectionHandler mockRunnable = mock(ConnectionHandler.class);
        when(mockWorkQueue.take()).thenReturn(() -> {});

        WorkerPool workerPool = new WorkerPool(size, mockWorkQueue);
        workerPool.offer(mockRunnable);

        verify(mockWorkQueue).put(mockRunnable);
    }

    @Test
    public void shouldWaitBeforeKillIfWorkQueueIsNotEmpty() throws Exception {
        final int size = 12;

        WorkQueue mockWorkQueue = mock(WorkQueue.class);
        when(mockWorkQueue.take()).thenReturn(() -> {});
        when(mockWorkQueue.isEmpty()).thenReturn(false);

        Runnable r1 = () -> {
            try {
                WorkerPool workerPool = new WorkerPool(size, mockWorkQueue);
                workerPool.kill();

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread workerPoolThread = new Thread(r1);
        workerPoolThread.start();
        Thread.sleep(1000);


        /*
         * mockWorkQueue always returns isEmpty=false,
         * so worker pool thread should wait forever
         */
        assertThat(workerPoolThread.isAlive(), is(true));

    }


    @Test
    public void shouldInterruptOnKillIfWorkQueueIsEmpty() throws Exception {
        final int size = 12;

        WorkQueue mockWorkQueue = mock(WorkQueue.class);
        when(mockWorkQueue.take()).thenReturn(() -> {});
        when(mockWorkQueue.isEmpty()).thenReturn(true);

        WorkerPool workerPool = new WorkerPool(size, mockWorkQueue);
        workerPool.kill();
        sleep(1000);

        for (WorkerThread worker : workerPool.getWorkers()) {
            assertThat(worker.isAlive(), is(false));
        }

    }

}
