package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.servlet.http.HttpServlet;

import javax.servlet.ServletException;

/**
 * @author rtv
 */
public interface ServletManager {

    HttpServlet launch(ServletConfig config) throws ServletException, ReflectiveOperationException;
    HttpServlet match(String uri);
    void shutdown();

}
