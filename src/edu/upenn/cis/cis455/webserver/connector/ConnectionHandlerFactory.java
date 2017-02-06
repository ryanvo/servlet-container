package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.thread.WorkQueue;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class ConnectionHandlerFactory {

    private static Logger log = LogManager.getLogger(ConnectionHandlerFactory.class);

    public static ConnectionHandler create(Container container,
                                           int poolSize,
                                           int workQueueSize) {


        WorkQueue requestQueue = new WorkQueue(workQueueSize);
        WorkerPool workerPool = new WorkerPool(poolSize, requestQueue);

        ConnectionManager manager = new ConnectionManager(workerPool);
        container.getContext().setAttribute("ConnectionManager", manager);

        RequestProcessor requestProcessor = new HttpRequestProcessor();

        return new ConnectionHandler(manager, container, requestProcessor);
    }

}
