package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.http.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.http.HttpRequest;

import java.io.IOException;

/**
 * @author rtv
 */
public interface RequestProcessor {

    void process(HttpRequest request) throws IOException, BadRequestException;

}
