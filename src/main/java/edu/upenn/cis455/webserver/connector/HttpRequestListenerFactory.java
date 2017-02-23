package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.Container;
import edu.upenn.cis455.webserver.thread.WorkQueue;
import edu.upenn.cis455.webserver.thread.WorkerPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class HttpRequestListenerFactory {

    private static Logger log = LogManager.getLogger(HttpRequestListenerFactory.class);

    public static HttpRequestListener create(Container container,
                                           int poolSize,
                                           int workQueueSize) {


        WorkQueue requestQueue = new WorkQueue(workQueueSize);
        WorkerPool workerPool = new WorkerPool(poolSize, requestQueue);
        ConnectionManager manager = new ConnectionManager(workerPool);

        RequestProcessor requestProcessor = new HttpRequestProcessor();
        ResponseProcessor responseProcessor = new HttpResponseProcessor();

        return new HttpRequestListener(manager, container, requestProcessor, responseProcessor);
    }

}
