package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;

import java.io.IOException;

/**
 * @author rtv
 */
public interface Container {
    void start()  throws Exception;

    void dispatch(HttpRequest req, HttpResponse resp) throws Exception;

    HttpServlet getMapping(String url);

    void shutdown();

    ServletContext getContext();
}
