package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rtv
 */
public class WebXmlParser {

    private String webXmlPath;
    private WebXml webXml;
    private ServletContext context;
    private Map<String, HttpServlet> servlets;

    public WebXmlParser(String webXmlPath) throws IOException {
        this.webXmlPath = webXmlPath;
    }

    public ServletContext createContext(WebXml webXml) {

        ServletContext context = new ServletContext();
        for (String param : webXml.contextParams.keySet()) {
            context.setInitParam(param, webXml.contextParams.get(param));
        }


        return context;

    }

    public Map<String, HttpServlet> createServlets(WebXml webXml, ServletContext
            context) throws Exception {
        Map<String,HttpServlet> servlets = new HashMap<>();
        for (String servletName : webXml.servletNames.keySet()) {
            ServletConfig config = new ServletConfig(servletName, context);
            String className = webXml.servletNames.get(servletName);
            Class servletClass = Class.forName(className);

            HttpServlet servlet = (HttpServlet) servletClass.newInstance();
            Map<String,String> servletParams = webXml.initParams.get(servletName);
            if (servletParams != null) {
                for (String param : servletParams.keySet()) {
                    config.setInitParam(param, servletParams.get(param));
                }

            }
            servlets.put(servletName, servlet);
        }
        return servlets;

    }

    public WebXml getWebXml() {
        return webXml;
    }

    public ServletContext getContext() {
        return context;
    }

    public Map<String, HttpServlet> getServlets()  {
        return servlets;
    }
}
