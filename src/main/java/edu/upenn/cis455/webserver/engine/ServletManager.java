package edu.upenn.cis455.webserver.engine;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Map;

/**
 * @author rtv
 */
public interface ServletManager {

    HttpServlet launch(String servletName, WebXmlHandler webXml) throws ServletException, ReflectiveOperationException;
    Map<String, HttpServlet> getServlets();
    void shutdown();

}
