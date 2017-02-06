package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import edu.upenn.cis.cis455.webserver.connector.RequestProcessor;
import edu.upenn.cis.cis455.webserver.thread.WorkQueue;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class WebAppContainerFactory {

    private static Logger log = LogManager.getLogger(WebAppContainerFactory.class);

    public static WebAppContainer create(String rootDirectory, WebXmlHandler webXml) {

          /* Create ServletContext from web.xml */
        ServletContextBuilder contextBuilder = new ServletContextBuilder();
        ServletContext context = contextBuilder.setRealPath(rootDirectory)
                                                .setContextParams(webXml.getContextParams())
                                                .build();

        /* Create WebAppContainer and composite ServletManager */
        ServletManager servletManager = new ServletManager(webXml, context);
        return new WebAppContainer(servletManager);

    }

}