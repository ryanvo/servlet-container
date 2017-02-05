package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;

import java.io.IOException;

/**
 * @author rtv
 */
public interface Container {
//    void launchServlets() throws IOException, ParseException, InstantiationException;

    void dispatch(HttpRequest req, HttpResponse resp) throws IOException;


//    void shutdown();

    ServletContext getContext();
}
