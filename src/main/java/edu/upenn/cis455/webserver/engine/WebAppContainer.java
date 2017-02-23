package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.engine.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * WebAppContainer holds all of the web applications and their servlets.
 * It receives HTTP requests from ConnectionHandler and directs them to
 * the appropriate servlet based on the url.
 * @author rtv
 */
public class WebAppContainer implements Container {

    private static Logger log = LogManager.getLogger(WebAppContainer.class);

    private Map<String, WebApp> webAppByName = new ConcurrentHashMap<>();
    private Map<String, AppContext> contextByName = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByPattern = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByWildcardPattern = new ConcurrentHashMap<>();
    private SessionManager sessionManager;

    public WebAppContainer(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void init(String rootDirectory) throws ServletException {

        ServletContextBuilder contextBuilder = new ServletContextBuilder();
        AppContext context = contextBuilder.setRealPath(rootDirectory)
                .setName("Default Servlets")
                .build();

        ServletConfigBuilder configBuilder = new ServletConfigBuilder();

        HttpServlet defaultServlet = new DefaultServlet();
        defaultServlet.init(configBuilder.setName("Default").setContext(context).build());

        HttpServlet controlServlet = new ControlServlet();
        controlServlet.init(configBuilder.setName("Control").setContext(context).build());


        HttpServlet shutdownServlet = new ShutdownServlet();
        shutdownServlet.init(configBuilder.setName("Shutdown").setContext(context).build());

        webAppByName.put("Default Servlets", webApp); //TODO make sure these are removed on deletion
        contextByName.put("Default Servlets", context);

        servletByPattern.put(Pattern.compile("/+control/*$"), controlServlet);
        servletByPattern.put(Pattern.compile("/+shutdown/*$"), shutdownServlet);

    }

    public WebApp startApp(String rootDirectory, WebXmlHandler webXml) throws ServletException,
            ReflectiveOperationException {

        /* Create AppContext from web.xml */
        ServletContextBuilder contextBuilder = new ServletContextBuilder();
        AppContext context = contextBuilder.setRealPath(rootDirectory)
                .setContextParams(webXml.getContextParams())
                .setName(webXml.getWebAppName())
                .build();

        WebApp webApp = new WebApp(context);
        String webAppName = webApp.getContext().getServletContextName();

        webAppByName.put(webAppName, webApp); //TODO make sure these are removed on deletion
        contextByName.put(webAppName, context);

        webApp.getContext().setAttribute("SessionManager", sessionManager);
        webApp.launchServlets(webXml);

        return webApp;
    }

    @Override
    public void dispatch(HttpRequest req, HttpResponse resp) throws ServletException, IOException {

        HttpServlet servlet = match(req.getRequestURI());

        servlet.service(req, resp);

    }


    @Override
    public HttpServlet match(String uri) {

        for (Pattern pattern : servletByPattern.keySet()) {
            Matcher uriMatcher = pattern.matcher(uri);
            if (uriMatcher.matches()) {

                log.info(String.format("uri:%s | servletName:%s | pattern:%s", uri,
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

        log.info(String.format("Uri:%s mapped to default http", uri));
        return defaultServlet;
    }


    //TODO
    public AppContext getContext(String appName) {
        return context;
    }

}
