package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.xml.ServletConfigBuilder;
import edu.upenn.cis.cis455.webserver.engine.xml.ServletContextBuilder;
import edu.upenn.cis.cis455.webserver.engine.xml.WebXmlHandler;

/**
 * @author rtv
 */
public class WebContainerFactory {

    public static WebContainer create(String webXmlPath, String rootDirectory) {
        WebXmlHandler webXml = new WebXmlHandler(webXmlPath);
        ServletConfigBuilder configBuilder = new ServletConfigBuilder();
        ServletContextBuilder contextBuilder = new ServletContextBuilder();
        return new WebContainer(webXml, rootDirectory, contextBuilder, configBuilder);
    }

}
