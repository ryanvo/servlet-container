package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.container.WebXmlHandler;
import edu.upenn.cis.cis455.webserver.servlet.HttpServlet;
import edu.upenn.cis.cis455.webserver.container.ServletContainer;
import edu.upenn.cis.cis455.webserver.container.HttpConnectionManager;
import edu.upenn.cis.cis455.webserver.servlet.DefaultServlet;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis.cis455.webserver.thread.WorkExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class ConcurrentServerFactory {

    private static Logger log = LogManager.getLogger(ConcurrentServerFactory.class);

    public static ConcurrentServer create(String webXml, String rootDirectory, int poolSize, int workQueueSize)
            throws Exception {

        WorkerPool workQueue = new WorkerPool(workQueueSize);
        WorkExecutorService exec = new WorkExecutorService(poolSize, workQueue);

        HttpConnectionManager manager = new HttpConnectionManager(exec);

        HttpServlet defaultServlet = new DefaultServlet(rootDirectory, manager);

        WebXmlHandler containerConfig = new WebXmlHandler(webXml);
        ServletContainer container = new ServletContainer(containerConfig, defaultServlet);

//        DefaultServlet servlet = new DefaultServlet(rootDirectory, manager);

        log.info(String.format("Factory Created Server at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new ConcurrentServer(container, exec);
    }

}
