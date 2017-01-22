package edu.upenn.cis.cis455.webserver.servlet;

import java.util.Map;

/**
 * @author rtv
 */
public class ServletContainer {
    private ContainerConfig config;
    private Map<String, HttpServlet> servlets;
    private ServletContext context;

    public ServletContainer(ContainerConfig config) {
        this.config = config;
        this.servlets = config.getServlets();
        this.context = config.getContext();
    }

    public void start() {

        /* Initialize and start each servlet */
        for (String servlet : servlets.keySet()) {
            for (String param : config.initParams.keySet()) {
                ServletConfig config = new ServletConfig(servlet, context);
                config.setInitParam(param, this.config.initParams.get(servlet)
                        .get(param));

                /* Get ServletConfig and init servlet */
                ServletConfig con = servlets.get(servlet).getServletConfig();
                servlets.get(servlet).init(con);
            }
        }
    }

    public void shutdown() {

        for (String servletName : config.getServlets().keySet()) {
            servlets.get(servletName).destroy();
        }
    }

    public ServletConfig getConfig() {
        return new ServletConfig(config.getServletName(), context);
    }

    public ServletContext getContext() {
        return context;
    }

}
