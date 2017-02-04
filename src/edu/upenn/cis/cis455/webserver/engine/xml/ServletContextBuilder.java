package edu.upenn.cis.cis455.webserver.engine.xml;

import edu.upenn.cis.cis455.webserver.engine.ServletContext;

import java.util.Map;

/**
 * @author rtv
 */
public class ServletContextBuilder {

    public ServletContext build(String realPath, Map<String, String> contextParams) {

        ServletContext servletContext = new ServletContext();
        for (String param : contextParams.keySet()) {
            servletContext.setInitParam(param, contextParams.get(param));
        }

        servletContext.setRealPath(realPath);
        return servletContext;
    }

}