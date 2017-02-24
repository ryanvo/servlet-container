package edu.upenn.cis455.webserver.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Manages all the servlets contained within a single web application
 * @author rtv
 */
public class WebApp implements ServletManager {

    private static Logger log = LogManager.getLogger(WebApp.class);

    private Map<String, HttpServlet> servlets = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByPattern = new ConcurrentHashMap<>();
    private Map<Pattern, HttpServlet> servletByWildcardPattern = new ConcurrentHashMap<>();

    private AppContext context;
    private String name;

    public WebApp(AppContext context) {
        this.context = context;
        this.name = context.getServletContextName();
    }

    /**
     * Uses the webXml configuration file to load and start all servlets
     * @param webXmlHandler
     * @throws ServletException
     * @throws ReflectiveOperationException
     */
    public void launchServlets(WebXmlHandler webXmlHandler) throws ServletException, ReflectiveOperationException {

        for (String servletName : webXmlHandler.getServletNames()) {

                launch(servletName, webXmlHandler);

            log.info("Initialized servlet=" + servletName);

        }
    }

    /**
     * Used to launch an individual servlet
     * @param servletName
     * @param webXml
     * @return HTTPServlet that was started
     * @throws ServletException
     * @throws ReflectiveOperationException
     */
    @Override
    public HttpServlet launch(String servletName, WebXmlHandler webXml) throws ServletException, ReflectiveOperationException {

        ServletConfigBuilder configBuilder = new ServletConfigBuilder();
        ServletConfig config = configBuilder.setName(servletName)
                .setContext(context)
                .setInitParams(webXml.getServletInitParamsByName(servletName))
                .build();


        Class servletClass = Class.forName(webXml.getClassByServletName(config.getServletName()));
        HttpServlet servlet = (HttpServlet) servletClass.newInstance();

        servlet.init(config);
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

        return servlet;
    }

    public Map<Pattern, HttpServlet> getServletByPattern() {
        return servletByPattern;
    }

    public Map<Pattern, HttpServlet> getServletByWildcardPattern() {
        return servletByWildcardPattern;
    }


    @Override
    public Map<String, HttpServlet> getServlets() {
        return servlets;
    }

    public AppContext getContext() {
        return context;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void shutdown() {
        for (HttpServlet servlet : servlets.values()) {
            log.info(String.format("Destroyed servlet: name=%s webapp=%s", servlet.getServletName(), getName()));
            servlet.destroy();
        }
    }

}
