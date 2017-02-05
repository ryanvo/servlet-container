package edu.upenn.cis.cis455.webserver.engine;

import java.util.Map;

/**
 * @author rtv
 */
public class ServletContextBuilder {

    public String realPath;
    public Map<String, String> contextParams;

    
    public ServletContextBuilder setRealPath(String realPath) {
        this.realPath = realPath;
        return this;
    }

    public ServletContextBuilder setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
        return this;
    }


    public ServletContext build() {
        return new ServletContext(this);
    }

}
