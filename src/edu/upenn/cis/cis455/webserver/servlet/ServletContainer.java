package edu.upenn.cis.cis455.webserver.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ServletContainer {
    private ContainerConfig containerConfig;
    private ContainerContext context;
    private Map<String, HttpServlet> servlets = new ConcurrentHashMap<>();

    public ServletContainer(ContainerConfig config) {
        this.containerConfig = config;
        this.context = config.getContext();
    }

    public void start() {

        /* Create a servlet for each mapping defined in web.xml */
        for (String servletName : containerConfig.getServletNames()) {

            String className = containerConfig.getClassByServletName(servletName);
            Class servletClass = null;
            try {
                servletClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            /* Create a servletConfig for each servlet by copying the init params parsed from web.xml */
            ServletConfig servletConfig = new ServletConfig(servletName, containerConfig.getContext());
            Map<String, String> servletParams = containerConfig.getInitParmsByServletName(servletName);
            if (servletParams != null) {
                for (String param : servletParams.keySet()) {
                    servletConfig.setInitParam(param, servletParams.get(param));
                }
            }

            /* Create servlet instance using the config and keep track in map */
            HttpServlet servlet = null;
            try {
                servlet = (HttpServlet) servletClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            servlet.init(servletConfig);
            servlets.put(servletName, servlet);
        }

    }

    public void shutdown() {

        for (String servletName : servlets.keySet()) {
            servlets.get(servletName).destroy();
        }

    }

//    public ServletConfig getContainerConfig() {
//        return new ServletConfig(containerConfig.getServletName(), context);
//    }

    public ContainerContext getContext() {
        return context;
    }

}
