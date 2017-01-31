package edu.upenn.cis.cis455.webserver.servlet.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

public class HttpRequest {

    private static Logger log = LogManager.getLogger(HttpRequest.class);

    private String method;
    private URI uri;
    private String type;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return type of request e.g. GET, POST...
     */
    public String getType() {
        return type;
    }

    /**
     * @return uri requested in status line
     */
    public URI getRequestURI() {
        return uri;
    }

}
