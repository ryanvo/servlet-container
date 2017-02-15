package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis455.webserver.servlet.exception.http.BadRequestException;

import java.io.IOException;

/**
 * @author rtv
 */
public interface RequestProcessor {

    void process(HttpRequest request) throws IOException, BadRequestException;

}
