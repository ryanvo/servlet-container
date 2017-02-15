package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.thread.WorkerPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

/**
 * Manages the HTTP requests delegated to HttpRequestProcessor. Maintains the status of each connector
 * and can issue a stop of the entire connector pool. Used for the Control Page
 */
public class ConnectionManager implements ThreadManager {

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
        log.debug(String.format("ConnectionManager received update threadId:%d, uri:%s", threadId, uri));
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
