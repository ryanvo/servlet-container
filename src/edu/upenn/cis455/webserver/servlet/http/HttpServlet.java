package edu.upenn.cis455.webserver.servlet.http;

import edu.upenn.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis455.webserver.engine.ServletContext;
import edu.upenn.cis455.webserver.servlet.exception.http.UnsupportedRequestException;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author rtv
 */
public abstract class HttpServlet {

    abstract public void init(ServletConfig config) throws ServletException;

    abstract public void destroy();


    public void service(HttpRequest request, HttpResponse response) throws ServletException {

        switch (request.getMethod()) {
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


        if (!response.isCommitted()) {
            try {
                response.flushBuffer();
            } catch (IllegalStateException e) {

            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }


    abstract public void doGet(HttpRequest req, HttpResponse resp) throws ServletException;

    abstract public void doHead(HttpRequest req, HttpResponse resp) throws ServletException;

    abstract public void doPost(HttpRequest req, HttpResponse resp) throws ServletException;

    abstract public ServletConfig getServletConfig();

    abstract public ServletContext getServletContext();

    abstract public String getServletName();

}