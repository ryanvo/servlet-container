package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.thread.MyBlockingQueue;
import edu.upenn.cis.cis455.webserver.thread.MyExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class MultiThreadedServerFactory {

    static Logger log = LogManager.getLogger(MultiThreadedServerFactory.class);

    static MultiThreadedServer create(String rootDirectory, int poolSize, int workQueueSize) {

        MyBlockingQueue workQueue = new MyBlockingQueue(workQueueSize);
        MyExecutorService exec = new MyExecutorService(poolSize, workQueue);
        HttpRequestManager manager = new HttpRequestManager(exec);
        HttpServlet servlet = new HttpServlet(rootDirectory, manager);

        log.info(String.format("Factory Created Server at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new MultiThreadedServer(exec, servlet);
    }

}
