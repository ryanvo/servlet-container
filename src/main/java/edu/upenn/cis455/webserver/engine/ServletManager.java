package edu.upenn.cis455.webserver.engine;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author rtv
 */
public interface ServletManager {

    HttpServlet launch(ServletConfig config) throws ServletException, ReflectiveOperationException;
    HttpServlet match(String uri);
    void shutdown();

}
