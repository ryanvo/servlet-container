package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.connector.*;
import edu.upenn.cis.cis455.webserver.engine.ServletContainer;
import edu.upenn.cis.cis455.webserver.thread.WorkQueue;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis.cis455.webserver.engine.WebXmlHandler;
import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class HttpConnectionHandlerFactory {

    private static Logger log = LogManager.getLogger(HttpConnectionHandlerFactory.class);

    public static ConnectionHandler create(String webXmlPath, String rootDirectory, int poolSize, int workQueueSize)
            throws Exception {

        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        Container container = new ServletContainer(webXml);

        WorkQueue requestQueue = new WorkQueue(workQueueSize);
        WorkerPool workerPool = new WorkerPool(poolSize, requestQueue);

        ConnectionManager manager = new ConnectionManager(workerPool);
        RequestProcessor requestProcessor = new HttpRequestProcessor(workerPool, container);

        log.info(String.format("Factory Created ConnectionHandler at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new ConnectionHandler(requestProcessor);
    }

}
