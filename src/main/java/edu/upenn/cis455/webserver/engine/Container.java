package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis455.webserver.engine.http.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * @author rtv
 */
public interface Container {

    void dispatch(HttpRequest req, HttpResponse resp) throws ServletException, IOException;
    WebApp startApp(String contextPath, WebXmlHandler webXml) throws ServletException, ReflectiveOperationException;
    HttpServlet match(String uri);
    AppContext getContext(String app);
    AppContext getContextByRequestUri(String uri);
}
