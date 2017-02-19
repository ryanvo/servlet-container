package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis455.webserver.servlet.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author rtv
 */
public interface Container {

    void dispatch(HttpRequest req, HttpResponse resp) throws ServletException, IOException;
    ServletContext getContext(String app);
}
