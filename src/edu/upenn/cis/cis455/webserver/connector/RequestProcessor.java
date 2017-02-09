package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.exception.http.BadRequestException;

import java.io.IOException;

/**
 * @author rtv
 */
public interface RequestProcessor {

    void process(HttpRequest request) throws IOException, BadRequestException;

}
