package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author rtv
 */
public class ServletContainer {

    private static Logger log = LogManager.getLogger(ServletContainer.class);

    private ServerSocket serverSocket;

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

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void dispatch(Socket connection) throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        line = in.readLine();

        log.info("Parsing HTTP Request: " + line);

        String[] statusLine = line.split(" ");
        String method = statusLine[0];

        URI uri = null;
        try {
            uri = new URI(statusLine[1]);
        } catch (URISyntaxException e) {
            log.error(e);
            throw new IOException();
        }

        HttpServlet servlet = null;
        String type = null;
        if (uri.getPath().matches("/+control/*$")) {
            type = "control";
            servlet = servlets.get(type);

        } else if (uri.getPath().matches("/+shutdown/*$")) {
            type = "shutdown";
            servlet = servlets.get(type);

        } else if (method.equals("GET")) {
            type = "get";
            servlet = servlets.get(type);
        }


        HttpRequest req = new HttpRequest();
        req.setMethod(method);
        req.setType(type);
        req.setUri(uri);
        servlet.service(req, new HttpResponse());

        log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));


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
