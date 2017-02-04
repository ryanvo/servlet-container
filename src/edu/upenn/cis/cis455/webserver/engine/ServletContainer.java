package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author rtv
 */
public class ServletContainer implements Container {

    private static Logger log = LogManager.getLogger(ServletContainer.class);

    private ServerSocket serverSocket;
    private WebXmlHandler webXml;

    private ServletContext context;
    private Map<String, HttpServlet> servlets = new ConcurrentHashMap<>();

    public ServletContainer(WebXmlHandler webXml) {
        this.webXml = webXml;
        this.context = new ServletContext(webXml);
    }

    @Override
    public void start() {

        /* Create a http for each mapping defined in web.xml */
        for (String servletName : webXml.getServletNames()) {

            try {

                String className = webXml.getClassByServletName(servletName);
                Class servletClass = Class.forName(className);

            /* Create a servletConfig for each http by copying the init params parsed from web.xml */
                ServletConfig servletConfig = new ServletConfig(servletName, context);
                Map<String, String> servletParams = webXml.getInitParamsByServletName(servletName);
                if (servletParams != null) {
                    for (String param : servletParams.keySet()) {
                        servletConfig.setInitParam(param, servletParams.get(param));
                    }
                }

            /* Create http instance using the config and keep track in map */
                HttpServlet servlet = (HttpServlet) servletClass.newInstance();

                servlet.init(servletConfig);
                servlets.put(servletName, servlet);

            } catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
                log.error("Error on http instantiation: " + servletName, e);
            }
        }

    }
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws IOException {

//        HttpServlet http;
//
//        http.service(req, resp);

    }

    @Override
    public HttpServlet getMapping(String url) {

        HttpServlet servlet = null;
        String type = null;
        if (url.matches("/+control/*$")) {
            type = "control";
            servlet = servlets.get(type);

        } else if (url.matches("/+shutdown/*$")) {
            type = "stop";
            servlet = servlets.get(type);

        } else {
//            http = defaultServlet;
        }

        return servlet;
    }

    @Override
    public void shutdown() {

        for (String servletName : servlets.keySet()) {
            servlets.get(servletName).destroy();
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("Trying to close server socket", e);
        }
    }

    @Override
    public ServletContext getContext() {
        return context;
    }

}
