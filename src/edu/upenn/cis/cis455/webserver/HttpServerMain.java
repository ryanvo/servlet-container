package edu.upenn.cis.cis455.webserver;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServerMain {

    private static Logger log = LogManager.getLogger(HttpServerMain.class);

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

//        int port = 8080;
//        String rootDirectory = "/home/cis555/hw1m1/www";

        try {
            HttpServer server = HttpServerFactory.create(rootDirectory,
                    webXmlPath, POOL_SIZE, WORK_QUEUE_SIZE);
            server.start(port);
        } catch (Exception e) {
            log.error(e);
            System.exit(-1);
        }

        log.info("Exiting Main");
        System.exit(0);
    }

}
