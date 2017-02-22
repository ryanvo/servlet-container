package edu.upenn.cis455.webserver.engine;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assembles the components of the multithreaded server
 */
public class WebAppContainerFactory {

    private static Logger log = LogManager.getLogger(WebAppContainerFactory.class);

    public static WebAppContainer create(String rootDirectory, WebXmlHandler webXml) {

          /* Create ApplicationContext from web.xml */
        ServletContextBuilder contextBuilder = new ServletContextBuilder();
        ApplicationContext context = contextBuilder.setRealPath(rootDirectory)
                .setContextParams(webXml.getContextParams())
                .setName(webXml.getWebAppName())
                .build();

        /* Create WebAppContainer and composite WebAppManager */
        WebAppManager webAppManager = new WebAppManager(webXml, context);
        return new WebAppContainer(webAppManager);

    }

}
