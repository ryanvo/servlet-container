package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * @author rtv
 */
public class WebAppContainer implements Container {

    private static Logger log = LogManager.getLogger(WebAppContainer.class);

    private ServletContext context;
    private ServletManager servletManager;

    public WebAppContainer(ServletManager servletManager) {
        this.servletManager = servletManager;
        this.context = servletManager.getContext();
    }


    public void start() throws IOException, InstantiationException {

        servletManager.launchServlets();

    }


    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws IOException {

        HttpServlet servlet = servletManager.match(req.getRequestURI());

        switch (req.getMethod().toUpperCase()) {
            case "GET":
                servlet.doGet(req, resp);
                break;
            case "POST":
                servlet.doPost(req, resp);
                break;
            case "HEAD":
                servlet.doHead(req, resp);
                break;
            default:
                log.error("Request Method not recognized: " + req.getMethod());
        }

    }

    @Override
    public ServletContext getContext() {
        return context;
    }

}
