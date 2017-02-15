package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.servlet.http.HttpServlet;

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
