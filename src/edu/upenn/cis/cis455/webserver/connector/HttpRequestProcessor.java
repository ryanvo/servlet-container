package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class HttpRequestProcessor implements RequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    private volatile boolean isRunning = true;
    private WorkerPool workerPool;
    private Container container;

    public HttpRequestProcessor(WorkerPool workerPool, Container container) {
        this.workerPool = workerPool;
        this.container = container;
    }

    @Override
    public void process(Socket connection) throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Executor Service is stopped");
        }
        workerPool.assign(new HttpRequestRunnable(connection, container));
    }

    @Override
    public void stop() {
        isRunning = false;
        workerPool.kill();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
