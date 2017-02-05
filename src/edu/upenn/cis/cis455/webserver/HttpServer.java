package edu.upenn.cis.cis455.webserver;


import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.ConnectionHandlerFactory;
import edu.upenn.cis.cis455.webserver.engine.*;
import org.apache.http.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class HttpServer {

    private static Logger log = LogManager.getLogger(HttpServer.class);

    public static void main(String args[]) {

        if (args.length != 5) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
        }

        int port = Integer.valueOf(args[0]);
        String rootDirectory = args[1];
        String webXmlPath = args[2];
        int POOL_SIZE = Integer.parseInt(args[3]);
        int WORK_QUEUE_SIZE = Integer.parseInt(args[4]);


        /* Parse web.xml file */
        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        try {
            webXml.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Create WebAppContainer and composite ServletManager */
        WebAppContainer container = WebAppContainerFactory.create(rootDirectory, webXml);
        log.info("WebAppContainer started: webXmlPath:" + webXmlPath + " rootDirectory:" + rootDirectory);

        /* Create ConnectionHandler for listening on port */
        ConnectionHandler connectionHandler = ConnectionHandlerFactory.create(container, POOL_SIZE, WORK_QUEUE_SIZE);
        log.info(String.format("Factory Created ConnectionHandler with %d threads, request queue of %d", POOL_SIZE,
                WORK_QUEUE_SIZE));

        /* Power up the servlets specified in web.xml */
        try {
            container.start();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* ConnectionHandler accepts incoming requests and dispatches them to WebAppContainer */
        try {
            connectionHandler.start(port);
        } catch (IOException e) {
            log.error("Failed to open ServerSocket", e);
        }

        log.info("Exiting Main");
        System.exit(0);


    }

}
