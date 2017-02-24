package edu.upenn.cis455.webserver;

import edu.upenn.cis455.webserver.connector.HttpRequestListener;
import edu.upenn.cis455.webserver.connector.HttpRequestListenerFactory;
import edu.upenn.cis455.webserver.engine.WebAppContainer;
import edu.upenn.cis455.webserver.engine.WebAppContainerFactory;
import edu.upenn.cis455.webserver.engine.WebXmlHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import java.io.IOException;



public class HttpServer {

    private static Logger log = LogManager.getLogger(HttpServer.class);

    public static void main(String args[]) {

        /* Default values */
        int POOL_SIZE = 64;
        int QUEUE_SIZE = 1024;

        if (args.length < 3) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
            System.out.println("Usage: <port> <root path> <web.xml path> [num threads] [blocking queue size]");
            System.exit(0);
        }

        int port = Integer.valueOf(args[0]);
        String rootDirectory = args[1];
        String webXmlPath = args[2];

        /* Initialize to default values above if not provided as argument */
        int poolSize = (args.length < 4) ? POOL_SIZE : Integer.parseInt(args[3]);
        int workQueueSize = (args.length < 5) ? QUEUE_SIZE : Integer.parseInt(args[4]);


        /* Parse web.xml file */
        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        try {
            webXml.parse();
        } catch (SAXException|IOException e) {
            log.error("Error reading web.xml: " + webXmlPath, e);
            System.exit(1);
        }

        /* Create WebAppContainer to manage servlets */
        WebAppContainer container = WebAppContainerFactory.create();

        /* Create HttpRequestListener to listening for requests */
        HttpRequestListener requestListener = HttpRequestListenerFactory.create(container, poolSize, workQueueSize);
        log.info(String.format("Factory Created ConnectionHandler with %d threads, request queue limit of %d",
                poolSize,
                workQueueSize));

        /* Provide Web App Container access to thread pool for control page servlet */
        container.setManager(requestListener.getManager());



        /*
         * Start the container at the specified directory then launch
         * all servlets specified in initial web.xml
         */
        try {
            log.info("WebAppContainer starting up servlets");
            container.init(rootDirectory);
            container.startApp(rootDirectory, webXml);
            log.info("WebAppContainer started with webXmlPath:" + webXmlPath + ", rootDirectory:" + rootDirectory);
        } catch (ServletException|ReflectiveOperationException e) {
            log.error("Error starting servlets", e);
            System.exit(1);
        }



        /*
         *  Start listening for incoming requests on the specified port and
         *  dispatch them to WebAppContainer for servicing
         */
        try {
            log.info("RequestListener listening on port:" + port);
            requestListener.start(port);
        } catch (IOException e) {
            log.error("Failed to open socket", e);
            System.exit(1);
        }

        log.info("Exiting Main...Waiting for open connections to finish...");

    }

}
