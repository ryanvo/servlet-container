package edu.upenn.cis455.webserver.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ServletConfigBuilder {

    public String name;
    public AppContext context;
    public Map<String, String> initParams = new HashMap<>();


    public ServletConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ServletConfigBuilder setContext(AppContext context) {
        this.context = context;
        return this;
    }

    public ServletConfigBuilder setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
        return this;
    }

//    public String getInitParam(String key) {
//        return initParams.get(key);
//    }
//    public ServletConfigBuilder addInitParam(String key, String val) {
//        initParams.put(key, val);
//        return this;
//    }

    public ServletConfig build() {

        if (initParams == null) {
            initParams = new ConcurrentHashMap<>();
        }
        return new ServletConfig(this);
    }


}