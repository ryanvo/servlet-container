package edu.upenn.cis.cis455.webserver.engine.http;

import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.exception.UnsupportedRequestException;

import javax.servlet.ServletException;

/**
 * @author rtv
 */
public abstract class HttpServlet {

    abstract public void init(ServletConfig config) throws ServletException;

    abstract public void destroy();


    public void service(HttpRequest request, HttpResponse response) throws ServletException {

        switch (request.getMethod().toUpperCase()) {
            case "GET":
                doGet(request, response);
                break;
            case "POST":
                doPost(request, response);
                break;
            case "HEAD":
                doHead(request, response);
                break;
            default:
                throw new ServletException(new UnsupportedRequestException());
        }
    }


    abstract public void doGet(HttpRequest req, HttpResponse resp) throws ServletException;

    abstract public void doHead(HttpRequest req, HttpResponse resp) throws ServletException;

    abstract public void doPost(HttpRequest req, HttpResponse resp) throws ServletException;

    abstract public ServletConfig getServletConfig();

    abstract public ServletContext getServletContext();

    abstract public String getServletName();

}