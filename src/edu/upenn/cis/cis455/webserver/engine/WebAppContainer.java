package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
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


    public void start() throws IOException, ServletException {

        webAppManager.launchServlets();

    }


    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws ServletException {

        HttpServlet servlet = webAppManager.match(req.getRequestURI());

        servlet.service(req, resp);

    }

    public ServletContext getContext(String app) {
        return context;
    }

}
