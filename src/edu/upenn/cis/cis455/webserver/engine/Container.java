package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.servlet.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.servlet.HttpServlet;

import java.io.IOException;

/**
 * @author rtv
 */
public interface Container {
    void start();

    void dispatch(HttpRequest req, HttpResponse resp) throws IOException;

    HttpServlet getMapping(String url);

    void shutdown();

    ServletContext getContext();
}
