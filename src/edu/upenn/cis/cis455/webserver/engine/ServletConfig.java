package edu.upenn.cis.cis455.webserver.engine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ServletConfig {

    private String name;
    private ServletContext context;
    private Map<String,String> initParams;

    public ServletConfig(String name, ServletContext context) {
        this.name = name;
        this.context = context;
        this.initParams = new ConcurrentHashMap<>();

        /* Copy the init params */
        while (context.getInitParameterNames().hasMoreElements()) {
            String key = (String) context.getInitParameterNames().nextElement();
            initParams.put(key, context.getInitParameter(key));
        }

    }

    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    void setInitParam(String name, String value) {
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

