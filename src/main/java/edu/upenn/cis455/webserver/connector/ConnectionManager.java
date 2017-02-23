package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.thread.WorkerPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the HTTP requests delegated to HttpRequestProcessor.
 *
 * Maintains the status of each connection and can shutdown the
 * the worker pool.
 *
 * Used by the control page to monitor thread statuses.
 */
public class ConnectionManager implements ProcessManager {

    private static Logger log = LogManager.getLogger(ConnectionManager.class);

    private final Map<Long, String> idToUri;
    private final WorkerPool workerPool;

    private boolean isRunning = true;

    public ConnectionManager(WorkerPool workerPool) {
        idToUri = new ConcurrentHashMap<>();
        this.workerPool = workerPool;
        for (Thread thread : workerPool.getWorkers()) {
            update(thread.getId(), "waiting");
        }
    }

    @Override
    public void assign(Runnable request) throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException();
        }
        workerPool.offer(request);
    }

    /**
     * Updates the status of a connector
     * @param threadId
     * @param uri currently serving
     */
    @Override
    public void update(long threadId, String uri) {
        idToUri.put(threadId, uri);
        log.debug(String.format("ConnectionManager received update threadId:%d, uri:%s", threadId, uri));
    }

    /**
     * Tells executor service to stop all threads
     */
    @Override
    public void shutdown() {
        isRunning = false;
        workerPool.kill();
    }

    @Override
    public Map<Long, String> getStatus() {
        Map<Long, String> status = new HashMap<>();

        for (Thread thread : workerPool.getWorkers()) {
            status.put(thread.getId(), idToUri.get(thread.getId()));
        }

        return status;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
