package edu.upenn.cis.cis455.webserver.engine;

import java.util.Map;

/**
 * @author rtv
 */
public class ServletConfigBuilder {

    public String name;
    public ServletContext context;
    public Map<String, String> initParams;


    public ServletConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ServletConfigBuilder setContext(ServletContext context) {
        this.context = context;
        return this;
    }

    public ServletConfigBuilder setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
        return this;
    }

    public ServletConfig build() {
        return new ServletConfig(this);
    }


}