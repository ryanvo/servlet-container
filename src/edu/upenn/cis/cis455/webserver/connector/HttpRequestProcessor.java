package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestProcessor {

    private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    private volatile boolean isRunning = true;
    private WorkerPool workerPool;
    private Container container;

    public HttpRequestProcessor(WorkerPool workerPool, Container container) {
        this.workerPool = workerPool;
        this.container = container;
    }

    public void process(Socket connection) throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Executor Service is stopped");
        }
        workerPool.assign(new HttpRequestRunnable(connection, container));
    }

    public void stop() {
        isRunning = false;
        workerPool.kill();
    }

    public boolean isRunning() {
        return isRunning;
    }

}
