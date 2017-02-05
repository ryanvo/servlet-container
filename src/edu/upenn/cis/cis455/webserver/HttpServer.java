package edu.upenn.cis.cis455.webserver;


import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.ConnectionHandlerFactory;
import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.engine.*;
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


        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        try {
            webXml.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServletContextBuilder contextBuilder = new ServletContextBuilder();
        ServletContext context = contextBuilder.setRealPath(rootDirectory)
                                               .setContextParams(webXml.getContextParams())
                                               .build();
        ServletManager servletManager = new ServletManager(webXml, context);
        WebContainer container = new WebContainer(servletManager);
        ConnectionHandler connectionHandler = ConnectionHandlerFactory.create(container, POOL_SIZE, WORK_QUEUE_SIZE);
        log.info("WebContainer started: webXmlPath:" + webXmlPath + " rootDirectory:" + rootDirectory);
        log.info(String.format("Factory Created ConnectionHandler with %d threads, request queue of %d", POOL_SIZE,
                WORK_QUEUE_SIZE));


        try {
            servletManager.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        connectionHandler.start(port);


        log.info("Exiting Main");
        System.exit(0);


    }

}
