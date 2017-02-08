package edu.upenn.cis.cis455.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author rtv
 */
public interface ServletManager {

    HttpServlet launch(ServletConfig config) throws IOException, ServletException ;
    HttpServlet match(String uri);
    void shutdown();

}
