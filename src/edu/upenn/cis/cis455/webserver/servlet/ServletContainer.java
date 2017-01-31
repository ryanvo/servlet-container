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


/**
 * @author rtv
 */
public class ServletContainer {

    private static Logger log = LogManager.getLogger(ServletContainer.class);

    private ServletContainerConfig config;
    private Map<String, Servlet> servlets;
    private ServletContext context;

    private ServerSocket serverSocket;

    public ServletContainer(ServletContainerConfig config) {
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

        Servlet servlet = null;
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
