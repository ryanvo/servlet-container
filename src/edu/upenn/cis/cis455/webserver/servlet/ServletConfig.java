package edu.upenn.cis.cis455.webserver.servlet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ServletConfig {

    private String name;
    private ContainerContext context;
    private Map<String,String> initParams;

    public ServletConfig(String name, ContainerContext context) {
        this.name = name;
        this.context = context;
        initParams = new ConcurrentHashMap<>();

    }

    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<>(keys);
        return atts.elements();
    }

    public ContainerContext getContext() {
        return context;
    }

    public String getServletName() {
        return name;
    }

    void setInitParam(String name, String value) {
        initParams.put(name, value);
    }
}

