package edu.upenn.cis.cis455.webserver;


import edu.upenn.cis.cis455.webserver.connector.HttpHandler;
import edu.upenn.cis.cis455.webserver.connector.HttpHandlerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServer {

    private static Logger log = LogManager.getLogger(HttpServer.class);

    public static void main(String args[]) {

        if (args.length != 2) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
        }

        int port = Integer.valueOf(args[0]);
        String rootDirectory = args[1];
        String webXmlPath = args[2];

        int POOL_SIZE = Integer.parseInt(args[3]);
        int WORK_QUEUE_SIZE = Integer.parseInt(args[4]);


        try {
            HttpHandler httpProcessor = HttpHandlerFactory.create(webXmlPath, rootDirectory, POOL_SIZE, WORK_QUEUE_SIZE);
            httpProcessor.start(port);
        } catch (Exception e) {
            log.error("Error constructing server. Exiting", e);
            System.exit(-1);
        }

        log.info("Exiting Main");
        System.exit(0);
    }

}
