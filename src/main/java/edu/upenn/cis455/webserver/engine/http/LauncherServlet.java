package edu.upenn.cis455.webserver.engine.http;


import edu.upenn.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis455.webserver.engine.SessionManager;
import edu.upenn.cis455.webserver.engine.WebApp;
import edu.upenn.cis455.webserver.engine.WebAppContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LauncherServlet extends HttpServlet {

    private static Logger log = LogManager.getLogger(LauncherServlet.class);

    private String name;
    private Map<String, String> initParams = new HashMap<>();
    private ServletConfig config;
    private ServletContext context;

    private WebAppContainer webAppContainer;
    private ConnectionManager connectionManager;
    private SessionManager sessionManager;


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


        webAppContainer = (WebAppContainer) config.getServletContext().getAttribute("Container");
        connectionManager = (ConnectionManager) config.getServletContext().getAttribute("ConnectionManager");
        sessionManager = (SessionManager) config.getServletContext().getAttribute("SessionManager");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(200);
        response.setContentType("text/html");


        PrintWriter writer = response.getWriter();
        writer.print("<!DOCTYPE html><html><head><title>Web Application Manager</title><body>");
        writer.print("<style>th, td {text-align: left;padding:7px;}</style>");

        writer.print("<h1><center>Web Application Manager</center></h1>");
        Map<String, List<String>> pattern = webAppContainer.getPatternByServletName();

        writer.print("<h3>Error Log</h3>");
        List<String> logFile = Files.readAllLines(Paths.get(getLoggerFileName("Error")));
        for (String line : logFile) {
            writer.println("<pre>" + line + "</pre>");
        }
        if (logFile.isEmpty()) {
            writer.println("<pre>No Errors :)</pre>");
        }

        for (WebApp app : webAppContainer.getWebAppByName().values()) {

            writer.print("<table style=\"width:100%\" border=\"0\"><tr style=\"font-size:20px\"><th><b>Application " +
                    "Name</b></th><th><b>Path</b></th></tr>");

            writer.print(String.format("<tr style=\"font-size:18px\"><td>%s</td><td>%s</td></tr>", app.getName(), app
                    .getContext().getRealPath
                    ("/")));
            writer.print("<table style=\"width:100%\" border=\"1\"><tr><th>Path</th><th>Servlet " +
                    "Display Name</th><th>Commands</th></tr>");

            for (String servletName : app.getServlets().keySet()) {

                String servletPatterns = "";
                writer.print("<tr>");

                for (String pat : pattern.get(servletName)) {
                    servletPatterns += String.format("<a href=\"%s\">%s</a>", pat, pat);
                }

                String servletCommands = "START STOP RELOAD";

                writer.print(String.format("<td>%s</td><td>%s</td><td>%s</td>", servletPatterns, servletName,
                        servletCommands));
                writer.print("</tr>");

            }

            writer.print("</table>");
        }

        writer.print("</table>");
        writer.print("</body></html>");

        log.info("Wrote Manage Page Response to Socket");
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

    public String getLoggerFileName(String type) {
        org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) log;
        Appender appender = logger.getAppenders().get(type);
        return ((FileAppender) appender).getFileName();
    }

}
