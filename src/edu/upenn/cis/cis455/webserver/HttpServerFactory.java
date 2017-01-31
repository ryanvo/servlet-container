package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletContainerConfig;
import edu.upenn.cis.cis455.webserver.servlet.ServletContainer;
import edu.upenn.cis.cis455.webserver.servlet.WebXmlParser;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpConnectionManager;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import edu.upenn.cis.cis455.webserver.thread.WorkExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class HttpServerFactory {

    private static Logger log = LogManager.getLogger(HttpServerFactory.class);

    public static HttpServer create(String rootDirectory, String webXmlPath,
                                    int poolSize, int workQueueSize) throws Exception {

        WorkerPool workQueue = new WorkerPool(workQueueSize);
        WorkExecutorService exec = new WorkExecutorService(poolSize, workQueue);

        HttpConnectionManager manager = new HttpConnectionManager(exec);
//        DefaultServlet servlet = new DefaultServlet(rootDirectory, manager);

        WebXmlParser webXmlParser = new WebXmlParser();
        ServletContainerConfig config = new ServletContainerConfig(webXmlPath, webXmlParser);

        ServletContainer container = new ServletContainer(config);


        log.info(String.format("Factory Created Server at %s, %d threads, request queue of %d",
                rootDirectory, poolSize, workQueueSize));

        return new HttpServer(exec, container);
    }

}
