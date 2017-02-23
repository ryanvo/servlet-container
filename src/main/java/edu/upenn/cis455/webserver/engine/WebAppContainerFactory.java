package edu.upenn.cis455.webserver.engine;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class WebAppContainerFactory {

    private static Logger log = LogManager.getLogger(WebAppContainerFactory.class);

    public static WebAppContainer create(String rootDirectory, WebXmlHandler webXml) {

        /* Create WebAppContainer and composite WebApp */
        SessionManager sessionManager = new SessionManager();
        return new WebAppContainer(sessionManager);

    }

}
