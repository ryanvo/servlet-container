package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;

import java.io.IOException;

/**
 * @author rtv
 */
public interface ResponseProcessor {

    void process(HttpResponse response) throws IOException;

}
