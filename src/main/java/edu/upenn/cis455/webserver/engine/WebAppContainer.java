package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis455.webserver.servlet.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;


/**
 * @author rtv
 */
public class WebAppContainer implements Container {

    private static Logger log = LogManager.getLogger(WebAppContainer.class);

    private ApplicationContext context;
    private WebAppManager webAppManager;

    public WebAppContainer(WebAppManager webAppManager) {
        this.webAppManager = webAppManager;
        this.context = webAppManager.
                getContext();
    }


    public void start() throws ServletException, ReflectiveOperationException {

        webAppManager.launchServlets();

    }


    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws ServletException, IOException {

        HttpServlet servlet = webAppManager.match(req.getRequestURI());

        servlet.service(req, resp);

    }

    public ApplicationContext getContext(String app) {
        return context;
    }

}
