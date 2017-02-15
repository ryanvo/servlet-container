package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis455.webserver.servlet.http.HttpResponse;

import javax.servlet.ServletException;

/**
 * @author rtv
 */
public interface Container {

    void dispatch(HttpRequest req, HttpResponse resp) throws ServletException;
    ServletContext getContext(String app);
}
