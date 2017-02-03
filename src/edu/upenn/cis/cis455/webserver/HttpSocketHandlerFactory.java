package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.connector.*;
import edu.upenn.cis.cis455.webserver.engine.WebXmlHandler;
import edu.upenn.cis.cis455.webserver.engine.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class HttpSocketHandlerFactory {

    private static Logger log = LogManager.getLogger(HttpSocketHandlerFactory.class);

    public static ConnectionHandler create(String webXmlPath, String rootDirectory, int poolSize, int workQueueSize)
            throws Exception {

        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        Container container = new Container(webXml);

        WorkQueue requestQueue = new WorkQueue(workQueueSize);
        WorkerPool workerPool = new WorkerPool(poolSize, requestQueue);

        HttpRequestProcessor requestProcessor = new HttpRequestProcessor(workerPool, container);
        ConnectionManager manager = new ConnectionManager(workerPool);

        log.info(String.format("Factory Created ConnectionHandler at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new ConnectionHandler(requestProcessor);
    }

}
