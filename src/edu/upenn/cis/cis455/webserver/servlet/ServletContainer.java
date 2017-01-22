package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.http.HttpServlet;
import java.util.Map;

/**
 * @author rtv
 */
public class ServletContainer {
    private WebXml webXml;
    private Map<String, HttpServlet> servlets;
    private ServletContext context;

    public ServletContainer(WebXml webXml) {
        this.webXml = webXml;
        this.servlets = webXml.getServlets();
        this.context = webXml.getContext();
    }

    public void start() {

        for (String servletName : webXml.getServlets().keySet()) {
            for (String param : webXml.initParams.keySet()) {
                ServletConfig config = new ServletConfig(servletName, context);
                config.setInitParam(param, webXml.initParams.get(servletName)
                        .get(param));

                servletName.init(config);

            }
        }
    }

    public void shutdown() {

        for (String servletName : webXml.getServlets().keySet()) {
            servletName.shutdown();
        }
    }

    public Map<String, HttpServlet> getServlets() {
        return webXml.getServlets();
    }

    public ServletConfig getConfig() {
        return webXml.getConfig();
    }

    public ServletContext getContext() {
        return webXml.getContext();
    }

}
