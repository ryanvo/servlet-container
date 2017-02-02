package edu.upenn.cis.cis455.webserver.container;

import edu.upenn.cis.cis455.webserver.servlet.HttpServlet;
import edu.upenn.cis.cis455.webserver.servlet.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
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
    private WebXmlHandler webXml;
    private ServletContext context;
    private Map<String, HttpServlet> servlets = new ConcurrentHashMap<>();
    private HttpServlet defaultServlet;

    public ServletContainer(WebXmlHandler webXml, HttpServlet defaultServlet) {
        this.webXml = webXml;
        this.context = webXml.getContext();
        this.defaultServlet = defaultServlet;
    }

    public void init() {

        /* Create a servlet for each mapping defined in web.xml */
        for (String servletName : webXml.getServletNames()) {

            String className = webXml.getClassByServletName(servletName);
            Class servletClass = null;
            try {
                servletClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            /* Create a servletConfig for each servlet by copying the init params parsed from web.xml */
            ServletConfig servletConfig = new ServletConfig(servletName, webXml.getContext());
            Map<String, String> servletParams = webXml.getInitParamsByServletName(servletName);
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

    public void serve(InputStream inputStream, OutputStream outputStream) throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
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

        HttpServlet servlet = getMapping(uri.getPath());


        HttpRequest req = new HttpRequest();
        req.setMethod(method);
//        req.setType(type);
        req.setUri(uri);
        servlet.service(req, new HttpResponse(outputStream));

        log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));


    }

    public HttpServlet getMapping(String url) {

        HttpServlet servlet = null;
        String type = null;
        if (url.matches("/+control/*$")) {
            type = "control";
            servlet = servlets.get(type);

        } else if (url.matches("/+shutdown/*$")) {
            type = "shutdown";
            servlet = servlets.get(type);

        } else {
            servlet = defaultServlet;
        }

        return servlet;
    }

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

//    public ServletConfig getContainerConfig() {
//        return new ServletConfig(webXml.getServletName(), context);
//    }

    public ServletContext getContext() {
        return context;
    }

}
