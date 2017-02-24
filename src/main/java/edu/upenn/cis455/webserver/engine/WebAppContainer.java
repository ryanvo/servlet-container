package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.connector.ProcessManager;
import edu.upenn.cis455.webserver.engine.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * WebAppContainer holds all of the web applications and their servlets.
 * It receives HTTP requests from ConnectionHandler and directs them to
 * the appropriate servlet based on the url.
 *
 * @author rtv
 */
public class WebAppContainer implements Container {

    private static Logger log = LogManager.getLogger(WebAppContainer.class);

    private Map<String, WebApp> webAppByName = new ConcurrentHashMap<>();
    private Map<String, AppContext> contextByAppName = new ConcurrentHashMap<>();
    private Map<String, AppContext> contextByServletName = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByPattern = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByWildcardPattern = new ConcurrentHashMap<>();
    private Map<String, List<String>> patternByServletName = new ConcurrentHashMap<>();
    private SessionManager sessionManager;
    private ProcessManager connectionManager;
    private HttpServlet defaultServlet;

    public WebAppContainer(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void init(String rootDirectory) throws ServletException {

        AppContextBuilder contextBuilder = new AppContextBuilder();
        AppContext context = contextBuilder.setRealPath(rootDirectory)
                .setName("Default")
                .build();

        ServletConfigBuilder configBuilder = new ServletConfigBuilder();
        context.setAttribute("ConnectionManager", connectionManager);
        context.setAttribute("SessionManager", sessionManager);
        context.setAttribute("Container", this);

        defaultServlet = new DefaultServlet();
        defaultServlet.init(configBuilder.setName("Default").setContext(context).build());
        contextByServletName.put(defaultServlet.getServletName(), context);

        HttpServlet controlServlet = new ControlServlet();
        controlServlet.init(configBuilder.setName("Control").setContext(context).build());
        contextByServletName.put(controlServlet.getServletName(), context);

        HttpServlet shutdownServlet = new ShutdownServlet();
        shutdownServlet.init(configBuilder.setName("Shutdown").setContext(context).build());
        contextByServletName.put(shutdownServlet.getServletName(), context);

        HttpServlet manageServlet = new ManageServlet();
        manageServlet.init(configBuilder.setName("Shutdown").setContext(context).build());
        contextByServletName.put(manageServlet.getServletName(), context);

        contextByAppName.put("Default", context);
//        webAppByName.put("Default Servlets", webApp); //TODO make sure these are removed on deletion

        HttpServlet launchServlet = new LauncherServlet();
        launchServlet.init(configBuilder.setName("Launch").setContext(context).build());
        contextByServletName.put(launchServlet.getServletName(), context);

        servletByPattern.put(Pattern.compile("/+control/*$"), controlServlet);
        servletByPattern.put(Pattern.compile("/+shutdown/*$"), shutdownServlet);
        servletByPattern.put(Pattern.compile("/+manage/*$"), manageServlet);
        servletByPattern.put(Pattern.compile("/+manage/launch/*$"), launchServlet);

    }

    @Override
    public WebApp startApp(String contextPath, WebXmlHandler webXml) throws ServletException,
            ReflectiveOperationException {

        /* Create AppContext from web.xml */
        AppContextBuilder contextBuilder = new AppContextBuilder();
        AppContext context = contextBuilder.setRealPath(contextPath)
                .setContextParams(webXml.getContextParams())
                .setName(webXml.getWebAppName())
                .build();

        WebApp webApp = new WebApp(context);
        context.setAttribute("SessionManager", sessionManager);
        webApp.launchServlets(webXml);

        /* Update container with servlet mappings for web app */
        servletByPattern.putAll(webApp.getServletByPattern());
        servletByWildcardPattern.putAll(webApp.getServletByWildcardPattern());
        patternByServletName.putAll(webXml.getPatternsByName());

        /* Update index for web app and context by display-name and context by servlet name*/
        webAppByName.put(webXml.getWebAppName(), webApp); //TODO make sure these are removed on deletion
        contextByAppName.put(webXml.getWebAppName(), context);
        for (Map.Entry<String, HttpServlet> servlet : webApp.getServlets().entrySet()) {
            contextByServletName.put(servlet.getKey(), context);
        }

        log.info(String.format("Started app: name=%s, count=%d, xml=%s", webXml.getWebAppName(), webApp.getServlets()
                .size(), webXml.getWebXmlPath()));

        return webApp;
    }

    /**
     * Forwards the http request to the appropriate servlet
     * @param req
     * @param resp
     * @throws ServletException a servlet error occurs during servicing of request
     * @throws IOException i/o error occurs
     */
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

                log.info(String.format("uri=%s servletName-%s pattern=%s", uri,
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


    /**
     * Maps application name (i.e. display-name in web.xml) to context
     * @param appName
     * @return ServletContext for app
     */
    @Override
    public AppContext getContext(String appName) {
        return contextByAppName.get(appName);
    }

    @Override
    public void shutdown() {

        for (WebApp app : webAppByName.values()) {
            app.shutdown();
        }

        connectionManager.shutdown();
    }

    public AppContext getContextByRequestUri(String uri) {
        return contextByServletName.get(match(uri).getServletName());
    }

    public Map<String, List<String>> getPatternByServletName() {
        return patternByServletName;
    }

    public void setManager(ProcessManager manager) {
        this.connectionManager = manager;
    }

    public Map<String, WebApp> getWebAppByName() {
        return webAppByName;
    }
}
