package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.servlet.ControlServlet;
import edu.upenn.cis.cis455.webserver.servlet.DefaultServlet;
import org.apache.http.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


/**
 * @author rtv
 */
public class WebContainer implements Container {

    private static Logger log = LogManager.getLogger(WebContainer.class);

    private ServerSocket serverSocket;
    private ServletContext context;
    private ServletManager servletManager;

    public WebContainer(ServletManager servletManager) {
        this.servletManager = servletManager;
        this.context = servletManager.getContext();
    }


    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws IOException {

        HttpServlet servlet = servletManager.match(req.getRequestURI());

        switch (req.getMethod()) {
            case "GET":
                servlet.doGet(req, resp);
                break;
            case "POST":
                servlet.doPost(req, resp);
                break;
            default:
                log.error("Request Method not recognized: " + req.getMethod());
        }

    }


    @Override
    public void shutdown() {

//        for (String servletName : servlets.keySet()) {
//            servlets.get(servletName).destroy();
//        }

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
