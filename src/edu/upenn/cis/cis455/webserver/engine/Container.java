package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author rtv
 */
public interface Container {

    void dispatch(HttpRequest req, HttpResponse resp) throws ServletException;
    ServletContext getContext(String app);
}
