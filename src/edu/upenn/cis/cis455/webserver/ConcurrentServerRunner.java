package edu.upenn.cis.cis455.webserver;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcurrentServerRunner {

    private static Logger log = LogManager.getLogger(ConcurrentServerRunner.class);

    public static void main(String args[]) {



        if (args.length != 2) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
        }

//        int port = Integer.valueOf(args[0]);
//        String rootDirectory = args[1];

        int port = 8080;
        String rootDirectory = "/home/cis555/hw1m1/www";
        int POOL_SIZE = 8;
        int WORK_QUEUE_SIZE = 16;

        ConcurrentServer server = ConcurrentServerFactory.create(rootDirectory, POOL_SIZE, WORK_QUEUE_SIZE);
        server.start(port);

        log.info("Exiting Main");
        System.exit(0);
    }

}