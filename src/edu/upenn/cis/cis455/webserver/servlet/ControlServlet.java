package edu.upenn.cis.cis455.webserver.servlet;


import edu.upenn.cis.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis.cis455.webserver.engine.ServletConfig;
import edu.upenn.cis.cis455.webserver.engine.ServletContext;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.servlet.io.ChunkedWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class ControlServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(ControlServlet.class);

    private final String HTTP_VERSION = "HTTP/1.1";
    private ConnectionManager manager;
    private Map<String, String> initParams;

    public ControlServlet(ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public void init(ServletConfig config)  throws ServletException {

        Enumeration paramNames = config.getInitParameterNames();
        while(paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

    }

    @Override
    public void destroy()  {



    }

    public void doGet(HttpRequest request, HttpResponse response) throws ServletException {
        log.info(getServletName() + " Serving Control Page Request");

        try (ChunkedWriter writer = new ChunkedWriter(response.getOutputStream())) {

            response.setStatus(200, "OK");
            response.setContentType("text/html");

            writer.write("<html><body><h1>Control Panel</h1>");
            writer.write("<p><h2>Thread &nbsp; &nbsp; &nbsp; &nbsp;Running</h2></p>");

            for (Map.Entry<Long, String> status : manager.getStatus().entrySet()) {
                long threadId = status.getKey();
                writer.write("<p>");
                writer.write(String.valueOf(threadId));
                writer.write("&nbsp; &nbsp; &nbsp; &nbsp &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
                        " &nbsp; &nbsp;");
                writer.write(status.getValue());
            }

            writer.write("<p><a href=\"/shutdown/\">Shutdown</a></p></body></html>");
            writer.close();
        } catch (IOException e) {
            throw new ServletException(e);//TODO http server error
        }
        try {
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Wrote Control Page Response to Socket");
    }

    @Override
    public void doHead(HttpRequest req, HttpResponse resp) throws ServletException  {

    }

    @Override
    public void doPost(HttpRequest req, HttpResponse resp)  throws ServletException  {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getServletName() {
        return null;
    }


}
