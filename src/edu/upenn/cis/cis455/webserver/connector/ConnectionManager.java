package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.thread.WorkerPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the HTTP requests delegated to HttpRequestProcessor. Maintains the status of each connector
 * and can issue a stop of the entire connector pool. Used for the Control Page
 */
public class ConnectionManager implements ThreadManager {

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
    public void update(long threadId, String uri) {
        idToUri.put(threadId, uri);
    }

    /**
     * Tells executor service to stop all threads
     */
    public void shutdown() {
        isRunning = false;
        workerPool.kill();
    }

    public Map<Long, String> getStatus() {
        Map<Long, String> status = new HashMap<>();

        for (Thread thread : workerPool.getWorkers()) {
            status.put(thread.getId(), idToUri.get(thread.getId()));
        }

        return status;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
