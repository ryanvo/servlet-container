package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.engine.xml.ServletConfigBuilder;
import edu.upenn.cis.cis455.webserver.engine.xml.ServletContextBuilder;
import edu.upenn.cis.cis455.webserver.engine.xml.WebXmlHandler;
import edu.upenn.cis.cis455.webserver.servlet.ControlServlet;
import org.apache.http.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author rtv
 */
public class WebContainer implements Container {

    private static Logger log = LogManager.getLogger(WebContainer.class);

    private ServerSocket serverSocket;
    private WebXmlHandler webXml;
    private String rootDirectory;
    private HttpServlet defaultServlet;

    private ServletContext context;
    private Map<String, HttpServlet> servlets = new ConcurrentHashMap<>();
    private Map<String, HttpServlet> servletByPattern = new ConcurrentHashMap<>();


    private ServletContextBuilder contextBuilder;
    private ServletConfigBuilder configBuilder;

    private ControlServlet controlServlet = new ControlServlet();

    public WebContainer(WebXmlHandler webXml,
                        String rootDirectory,
                        HttpServlet defaultServlet,
                        ServletContextBuilder contextBuilder,
                        ServletConfigBuilder configBuilder) {
        this.webXml = webXml;
        this.rootDirectory = rootDirectory;
        this.defaultServlet = defaultServlet;
        this.contextBuilder = contextBuilder;
        this.configBuilder = configBuilder;
    }

    @Override
    public void start() throws IOException, ParseException, InstantiationException {

        webXml.parse();
        this.context = contextBuilder.build(rootDirectory, webXml.getContextParams());


        for (String servletName : webXml.getServletNames()) {

            ServletConfig config = configBuilder.build(servletName, context, webXml.getInitParamsByServletName(servletName));

            log.debug("Initiating servlet: " + servletName);

            try {

                Class servletClass = Class.forName(webXml.getClassByServletName(servletName));
                HttpServlet servlet = (HttpServlet) servletClass.newInstance();



                servlet.init(config);
                servlets.put(servletName, servlet);
                String pattern = webXml.getNameByPatterns().get(servletName);
                servletByPattern.put(pattern, servlet);
                log.info("Started servlet: " + servletName);


            } catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {

                throw new InstantiationException();
            }
        }

        for (String name : webXml.getNameByPatterns().keySet()) {




        }

    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws IOException {

        HttpServlet servlet;
//
//        http.service(req, resp);
        if (req.getRequestURI().matches("/+control/*$")) {
            controlServlet.service(req, resp);

        } else if (req.getRequestURI().matches("/+shutdown/*$")) {
//            return defaultServlet;
        } else {
            defaultServlet.doGet(req, resp);
        }

    }

    @Override
    public HttpServlet getMapping(String uri) {


        for (String pattern : servletByPattern.keySet()) {
            if (uri.matches(pattern)) {
                return servletByPattern.get(pattern);
            }
        }

        if (uri.matches("/+control/*$")) {
            return defaultServlet;

        } else if (uri.matches("/+shutdown/*$")) {
            return defaultServlet;
        }

        return defaultServlet;
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
