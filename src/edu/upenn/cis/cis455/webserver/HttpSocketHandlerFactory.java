package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.connector.HttpConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.RequestProcessor;
import edu.upenn.cis.cis455.webserver.connector.WorkerPool;
import edu.upenn.cis.cis455.webserver.engine.WebXmlHandler;
import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class HttpSocketHandlerFactory {

    private static Logger log = LogManager.getLogger(HttpSocketHandlerFactory.class);

    public static HttpConnectionHandler create(String webXmlPath, String rootDirectory, int poolSize, int workQueueSize)
            throws Exception {

        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        Container container = new Container(webXml);

        WorkerPool workPool = new WorkerPool(workQueueSize);
        RequestProcessor exec = new RequestProcessor(poolSize, workPool);
        ConnectionManager manager = new ConnectionManager(container.getContext());

        log.info(String.format("Factory Created HttpConnectionHandler at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new HttpConnectionHandler(exec, container);
    }

}
