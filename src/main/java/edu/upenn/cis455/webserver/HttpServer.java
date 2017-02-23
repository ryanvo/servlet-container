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

        int POOL_SIZE = 64;
        int QUEUE_SIZE = 1024;

        if (args.length < 3) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
            System.exit(1);
        }

        int port = Integer.valueOf(args[0]);
        String rootDirectory = args[1];
        String webXmlPath = args[2];

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

        /* Create WebAppContainer manage servlets */
        WebAppContainer container = WebAppContainerFactory.create(rootDirectory, webXml);
        log.info("WebAppContainer created with webXmlPath:" + webXmlPath + ", rootDirectory:" + rootDirectory);

        /* Create HttpRequestListener to listening for requests */
        HttpRequestListener requestListener = HttpRequestListenerFactory.create(container, poolSize, workQueueSize);
        log.info(String.format("Factory Created ConnectionHandler with %d threads, request queue limit of %d",
                poolSize,
                workQueueSize));

        /* Power up the servlets specified in web.xml */
        try {
            log.info("WebAppContainer starting up servlets");
            container.start();
        } catch (ServletException|ReflectiveOperationException e) {
            log.error("Error starting servlets", e);
            System.exit(1);
        }

        /* Accept incoming requests and dispatch them to WebAppContainer */
        try {
            log.info("RequestListener listening on port:" + port);
            requestListener.start(port);
        } catch (IOException e) {
            log.error("Failed to open socket", e);
            System.exit(1);
        }

        log.info("Exiting Main");


    }

}
