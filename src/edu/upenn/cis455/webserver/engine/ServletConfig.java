package edu.upenn.cis455.webserver.engine;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ServletConfig implements javax.servlet.ServletConfig {

    private String name;
    private ServletContext context;
    private Map<String, String> initParams;

    public ServletConfig(ServletConfigBuilder builder) {
        name = builder.name;
        context = builder.context;
        initParams = new ConcurrentHashMap<>(builder.initParams);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    public void setInitParam(String name, String value) {
        initParams.put(name, value);
    }

    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }

    public String getServletName() {
        return name;
    }

    public ServletContext getServletContext() {
        return context;
    }

}

