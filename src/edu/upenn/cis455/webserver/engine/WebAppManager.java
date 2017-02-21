package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.connector.ConnectionManager;
import edu.upenn.cis455.webserver.servlet.ControlServlet;
import edu.upenn.cis455.webserver.servlet.DefaultServlet;
import edu.upenn.cis455.webserver.servlet.ShutdownServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rtv
 */
public class WebAppManager implements ServletManager {

    private static Logger log = LogManager.getLogger(WebAppManager.class);

    private WebXmlHandler webXml;
    private ApplicationContext context;

    private Map<Pattern, HttpServlet> servletByPattern = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByWildcardPattern = new ConcurrentHashMap<>();

    private Map<String, HttpServlet> servlets = new ConcurrentHashMap<>();

    private HttpServlet defaultServlet;
    private HttpServlet controlServlet;
    private HttpServlet shutdownServlet;


    public WebAppManager(WebXmlHandler webXml, ApplicationContext context) {
        this.webXml = webXml;
        this.context = context;
    }

    public void launchServlets() throws ServletException, ReflectiveOperationException {

        ServletConfigBuilder configBuilder = new ServletConfigBuilder();

        defaultServlet = new DefaultServlet(context.getRealPath("path"));
        controlServlet = new ControlServlet((ConnectionManager) context.getAttribute("ConnectionManager"));
        shutdownServlet = new ShutdownServlet();
        shutdownServlet.init(configBuilder.setName("Shutdown").setContext(context).build());

        servletByPattern.put(Pattern.compile("/+control/*$"), controlServlet);
        servletByPattern.put(Pattern.compile("/+shutdown/*$"), shutdownServlet);

        String servletName = "demo";

//        for (String servletName : webXml.getServletNames()) {
            log.info("Initiating servlet: " + servletName);

            ServletConfig config = configBuilder.setName(servletName)
                    .setContext(context)
                    .setInitParams(webXml.getServletInitParamsByName(servletName))
                    .build();

            HttpServlet servlet = launch(config);
            servlets.put(servletName, servlet);

            List<String> patterns = webXml.getPatternsByName().get(servletName);
            for (String pat : patterns) {

                if (pat.contains("*")) {
                    pat = pat.replace("*", ".*");
                    pat = pat + "/*$";

                    servletByWildcardPattern.put(Pattern.compile(pat), servlet);

                } else {
                    pat = pat + "/*$";

                    servletByPattern.put(Pattern.compile(pat), servlet);

                }

            }

            log.info("Started servlet: " + servletName);

//        }
    }

    @Override
    public HttpServlet launch(ServletConfig config) throws ServletException, ReflectiveOperationException {

//        Class servletClass = Class.forName(webXml.getClassByServletName(config.getServletName()));
        Class servletClass = Class.forName("ryan.DemoServlet");
        HttpServlet servlet = (HttpServlet) servletClass.newInstance();
        servlet.init(config);

        return servlet;
    }

    @Override
    public HttpServlet match(String uri) {

        for (Pattern pattern : servletByPattern.keySet()) {
            Matcher uriMatcher = pattern.matcher(uri);
            if (uriMatcher.matches()) {

                log.info(String.format("Uri:%s mapped to servletName:%s with servletPattern:%s", uri,
                        servletByPattern.get(pattern).getServletName(), pattern));

                return servletByPattern.get(pattern);
            }

        }

        for (Pattern pattern : servletByWildcardPattern.keySet()) {
            Matcher uriMatcher = pattern.matcher(uri);
            if (uriMatcher.matches()) {

                log.info(String.format("Uri:%s mapped to servletName:%s with servletPattern:%s", uri,
                        servletByWildcardPattern.get(pattern).getServletName(), pattern));

                return servletByWildcardPattern.get(pattern);
            }

        }

        return defaultServlet;
    }

    public ApplicationContext getContext() {
        return context;
    }

    @Override
    public void shutdown() {
        for (String servletName : servlets.keySet()) {
            servlets.get(servletName).destroy();
            log.info("Servlet destroyed servletName:" + servletName);
        }
    }

}
