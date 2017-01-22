package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.http.HttpRequestManager;
import edu.upenn.cis.cis455.webserver.servlet.DefaultServlet;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis.cis455.webserver.thread.WorkExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class MultiThreadedServerFactory {

    private static Logger log = LogManager.getLogger(MultiThreadedServerFactory.class);

    public static MultiThreadedServer create(String rootDirectory, int
            poolSize,
                                       int workQueueSize) {

        WorkerPool workQueue = new WorkerPool(workQueueSize);
        WorkExecutorService exec = new WorkExecutorService(poolSize, workQueue);
        HttpRequestManager manager = new HttpRequestManager(exec);
        DefaultServlet servlet = new DefaultServlet(rootDirectory, manager);

        log.info(String.format("Factory Created Server at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new MultiThreadedServer(exec, servlet);
    }

}
