package edu.upenn.cis455.webserver.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class AppContextBuilder {

    public String realPath;

    public String getRealPath() {
        return realPath;
    }

    public Map<String, String> getContextParams() {
        return contextParams;
    }

    public Map<String, String> contextParams;

    public String getName() {
        return name;
    }

    public String name;

    public AppContextBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public AppContextBuilder setRealPath(String realPath) {
        this.realPath = realPath;
        return this;
    }

    public AppContextBuilder setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
        return this;
    }


    public AppContext build() {

        if (contextParams == null) {
            contextParams = new ConcurrentHashMap<>();
        }
        return new AppContext(this);
    }

}
