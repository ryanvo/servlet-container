package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author rtv
 */
public interface ResponseProcessor {

    void process(HttpResponse response, OutputStream out) throws IOException;

}
