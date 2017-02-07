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
    private WebAppManager webAppManager;

    public WebAppContainer(WebAppManager webAppManager) {
        this.webAppManager = webAppManager;
        this.context = webAppManager.getContext();
    }


    public void start() throws IOException, InstantiationException {

        webAppManager.launchServlets();

    }


    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws IOException {

        HttpServlet servlet = webAppManager.match(req.getRequestURI());

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

    public ServletContext getContext(String app) {
        return context;
    }

}
