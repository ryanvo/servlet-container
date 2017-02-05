package edu.upenn.cis.cis455.webserver.engine.http;

import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;

/**
 * @author rtv
 */
public interface HttpServlet {

    void init(ServletConfig config);
    void destroy();
//    void service(HttpRequest request, HttpResponse response);
    void doGet(HttpRequest req, HttpResponse resp);
    void doHead(HttpRequest req, HttpResponse resp);
    void doPost(HttpRequest req, HttpResponse resp);
    ServletConfig getServletConfig();
    ServletContext getServletContext();
    String getServletName();

}