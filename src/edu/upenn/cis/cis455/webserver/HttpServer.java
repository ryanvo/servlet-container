package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


public class HttpServer {

    static Logger log = Logger.getLogger(HttpServer.class);

    public static void main(String args[]) {

        int POOL_SIZE = 8;
        int WORK_QUEUE_SIZE = 16;

        if (args.length != 2) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
        }

        int port = Integer.valueOf(args[0]);
        String rootDirectory = args[1];

        BasicConfigurator.configure();

        MultiThreadedServer server = MultiThreadedServerFactory.create(rootDirectory, POOL_SIZE,
                WORK_QUEUE_SIZE);
        server.start(port);

        log.info("Exiting Main");
        System.exit(0);
    }

}
