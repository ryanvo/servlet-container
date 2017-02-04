package edu.upenn.cis.cis455.webserver;


import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.ConnectionHandlerFactory;
import edu.upenn.cis.cis455.webserver.engine.WebContainer;
import edu.upenn.cis.cis455.webserver.engine.WebContainerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServer {

    private static Logger log = LogManager.getLogger(HttpServer.class);

    public static void main(String args[]) {
//
        if (args.length != 5) {
            System.out.println("Name: Ryan Vo");
            System.out.println("SEAS Login: ryanvo");
        }

        int port = Integer.valueOf(args[0]);
        String rootDirectory = args[1];
        String webXmlPath = args[2];

        int POOL_SIZE = Integer.parseInt(args[3]);
        int WORK_QUEUE_SIZE = Integer.parseInt(args[4]);


        //TODO try catch
        WebContainer container = WebContainerFactory.create(webXmlPath, rootDirectory);
        log.info("WebContainer started: webXmlPath:" + webXmlPath + " rootDirectory:" + rootDirectory);
        ConnectionHandler connectionHandler = ConnectionHandlerFactory.create(container, POOL_SIZE, WORK_QUEUE_SIZE);
        connectionHandler.start(port);

        log.info("Exiting Main");
        System.exit(0);


    }

}
