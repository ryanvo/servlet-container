package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ContainerConfig;
import edu.upenn.cis.cis455.webserver.servlet.ServletContainer;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpConnectionManager;
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

    public static ConcurrentServer create(String webXml, String rootDirectory, int poolSize, int workQueueSize) {

        WorkerPool workQueue = new WorkerPool(workQueueSize);
        WorkExecutorService exec = new WorkExecutorService(poolSize, workQueue);
        HttpConnectionManager manager = new HttpConnectionManager(exec);


        ContainerConfig containerConfig = new ContainerConfig(webXml);
        ServletContainer container = new ServletContainer(containerConfig);

        DefaultServlet servlet = new DefaultServlet(rootDirectory, manager);

        ContainerConfig config = new ContainerConfig(webXml);


        log.info(String.format("Factory Created Server at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new ConcurrentServer(container, exec);
    }

}
