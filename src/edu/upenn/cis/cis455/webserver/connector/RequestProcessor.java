package edu.upenn.cis.cis455.webserver.connector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class RequestProcessor {

    private static Logger log = LogManager.getLogger(RequestProcessor.class);

    private volatile boolean isRunning = true;
    private WorkerPool queue;
    private Set<WorkerThread> threadPool = new HashSet<>();

    public RequestProcessor(int poolSize, WorkerPool queue) {
        this.queue = queue;

        for (int i = 0; i < poolSize; i++) {
            threadPool.add(new WorkerThread(queue));
        }

        for (Thread thread : threadPool) {
            thread.start();
        }
    }

    public void process(Runnable connection) throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Executor Service is stopped");
        }
        queue.put(connection);
    }

    public void stop() {
        isRunning = false;

        while (!queue.isEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {}
        }

        for (WorkerThread thread : threadPool) {
                thread.interrupt();
        }

        log.info("All threads stopped");
    }

    public Set<WorkerThread> threadPool() {
        return threadPool;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
