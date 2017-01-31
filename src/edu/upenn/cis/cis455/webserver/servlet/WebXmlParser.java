//package edu.upenn.cis.cis455.webserver.servlet;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author rtv
// */
//public class WebXmlParser {
//
//    private String webXmlPath;
//
//    public WebXmlParser(String webXmlPath) throws IOException {
//        this.webXmlPath = webXmlPath;
//    }
//
//    public ContainerContext createContext(ContainerConfig containerConfig) {
//
//        ContainerContext context = new ContainerContext();
//        for (String param : containerConfig.contextParams.keySet()) {
//            context.setInitParam(param, containerConfig.contextParams.get(param));
//        }
//        return context;
//
//    }
//
//    public Map<String, HttpServlet> createServlets(ContainerConfig containerConfig, ContainerContext
//            context) throws Exception {
//        Map<String,HttpServlet> servlets = new HashMap<>();
//        for (String servletName : containerConfig.servletNames.keySet()) {
//            ServletConfig config = new ServletConfig(servletName, context);
//            String className = containerConfig.servletNames.get(servletName);
//            Class servletClass = Class.forName(className);
//
//            HttpServlet servlet = (HttpServlet) servletClass.newInstance();
//            Map<String,String> servletParams = containerConfig.initParams.get(servletName);
//            if (servletParams != null) {
//                for (String param : servletParams.keySet()) {
//                    config.setInitParam(param, servletParams.get(param));
//                }
//
//            }
//            servlets.put(servletName, servlet);
//        }
//        return servlets;
//
//    }
//
//}
