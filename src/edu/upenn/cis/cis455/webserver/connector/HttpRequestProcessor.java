package edu.upenn.cis.cis455.webserver.connector;


import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class HttpRequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    private volatile boolean isRunning = true;
    private WorkerPool queue;
    private Set<WorkerThread> threadPool = new HashSet<>();
    private Container container;

    public HttpRequestProcessor(int poolSize, WorkerPool queue, Container container) {
        this.queue = queue;
        this.container = container;

        for (int i = 0; i < poolSize; i++) {
            threadPool.add(new WorkerThread(queue));
        }

        for (Thread thread : threadPool) {
            thread.start();
        }
    }

    public void process(Socket connection) throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Executor Service is stopped");
        }
        queue.put(new HttpRequestRunnable(connection, container));
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
