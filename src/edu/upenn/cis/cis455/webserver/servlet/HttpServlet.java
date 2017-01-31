package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpResponse;

/**
 * @author rtv
 */
public interface HttpServlet {

    void init(ServletConfig config);
    void destroy();
    void service(HttpRequest request, HttpResponse response);
    void doGet(HttpRequest req, HttpResponse resp);
    void doPost(HttpRequest req, HttpResponse resp);
    ServletConfig getServletConfig();
    ContainerContext getServletContext();
    String getServletName();

}