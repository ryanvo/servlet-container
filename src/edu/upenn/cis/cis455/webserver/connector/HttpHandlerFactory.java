package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.WebXmlHandler;
import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.servlet.ConnectionManager;
import edu.upenn.cis.cis455.webserver.engine.servlet.DefaultServlet;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class HttpHandlerFactory {

    private static Logger log = LogManager.getLogger(HttpHandlerFactory.class);

    public static HttpHandler create(String webXmlPath, String rootDirectory, int poolSize, int workQueueSize)
            throws Exception {

        WorkerPool workQueue = new WorkerPool(workQueueSize);
        HttpRequestProcessor exec = new HttpRequestProcessor(poolSize, workQueue);

        ConnectionManager manager = new ConnectionManager(exec);

        HttpServlet defaultServlet = new DefaultServlet(rootDirectory, manager);
        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        Container container = new Container(webXml);


        log.info(String.format("Factory Created HttpHandler at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new HttpHandler(container, exec);
    }

}
