package edu.upenn.cis.cis455.webserver.engine.xml;

import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;

import java.util.Map;

/**
 * @author rtv
 */
public class ServletConfigBuilder {

    public ServletConfig build(String name, ServletContext context, Map<String, String> servletNameInitParams) {

        ServletConfig servletConfig = new ServletConfig();
        if (servletNameInitParams != null) {
            for (String param : servletNameInitParams.keySet()) {
                servletConfig.setInitParam(param, servletNameInitParams.get(param));
            }
        }

        servletConfig.setName(name);
        servletConfig.setContext(context);
        return servletConfig;
    }


}