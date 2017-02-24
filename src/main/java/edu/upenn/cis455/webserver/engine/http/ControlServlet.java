package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.connector.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Services the /control page
 */
public class ControlServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(ControlServlet.class);

    private ConnectionManager manager;
    private String name;
    private Map<String, String> initParams = new HashMap<>();
    private ServletConfig config;
    private ServletContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.name = config.getServletName();
        this.config = config;
        this.context = config.getServletContext();

        Enumeration paramNames = config.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            initParams.put(key, config.getInitParameter(key));
        }

        manager = (ConnectionManager) config.getServletContext().getAttribute("ConnectionManager");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info(getServletName() + " Serving Control Page Request");
        response.setStatus(200);
        response.setContentType("text/html");


        PrintWriter writer = response.getWriter();
        writer.print("<html><body><h1>Control Panel</h1>");
        writer.print("<p><h2>Thread &nbsp; &nbsp; &nbsp; &nbsp;Running</h2></p>");

        for (Map.Entry<Long, String> status : manager.getStatus().entrySet()) {
            long threadId = status.getKey();
            writer.print("<p>");
            writer.print(String.valueOf(threadId));
            writer.print("&nbsp; &nbsp; &nbsp; &nbsp &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
                    " &nbsp; &nbsp;");
            writer.print(status.getValue());
        }

        writer.print("<p><a href=\"/shutdown/\">Shutdown</a></p></body></html>");

        log.info("Wrote Control Page Response to Socket");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getServletName() {
        return name;
    }


}
