package edu.upenn.cis.cis455.webserver.servlet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class ServletContainerConfig extends DefaultHandler {

    private int m_state = 0;
    private String servletName;
    private String m_paramName;
    private Map<String,String> servletNames = new ConcurrentHashMap<>();
    private Map<String,String> contextParams = new ConcurrentHashMap<>();
    private Map<String,Map<String,String>> initParams = new
            ConcurrentHashMap<>();

    private ServletContext context;
    private Map<String,Servlet> servlets = new ConcurrentHashMap<>();

    public ServletContainerConfig(String webXmlPath, WebXmlParser parser) throws Exception {
        try {
            File file = new File(webXmlPath);
            if (!file.exists()) {
                throw new IOException();
            }
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(file, this);

            context = createContext();
            servlets = createServlets(context);

        } catch (ParserConfigurationException |SAXException e) {
            throw new IOException();
        }
    }

    public String getServletName() {
        return servletName;
    }

    public Map<String, Servlet> getServlets() {
        return servlets;
    }

    public ServletContext getContext() {
        return context;
    }

    public ServletContext createContext() {

        ServletContext context = new ServletContext();
        for (String param : contextParams.keySet()) {
            context.setInitParam(param, contextParams.get(param));
        }
        return context;

    }

    public Map<String, Servlet> createServlets( ServletContext context) throws Exception {
        Map<String,Servlet> servlets = new HashMap<>();
        for (String servletName : servletNames.keySet()) {
            ServletConfig config = new ServletConfig(servletName, context);
            String className = servletNames.get(servletName);
            Class servletClass = Class.forName(className);

            Servlet servlet = (Servlet) servletClass.newInstance();
            Map<String,String> servletParams = initParams.get(servletName);
            if (servletParams != null) {
                for (String param : servletParams.keySet()) {
                    config.setInitParam(param, servletParams.get(param));
                }

            }
            servlets.put(servletName, servlet);
        }
        return servlets;

    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.compareTo("servlet-name") == 0) {
            m_state = 1;
        } else if (qName.compareTo("servlet-class") == 0) {
            m_state = 2;
        } else if (qName.compareTo("context-param") == 0) {
            m_state = 3;
        } else if (qName.compareTo("init-param") == 0) {
            m_state = 4;
        } else if (qName.compareTo("param-name") == 0) {
            m_state = (m_state == 3) ? 10 : 20;
        } else if (qName.compareTo("param-value") == 0) {
            m_state = (m_state == 10) ? 11 : 21;
        }
    }

    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length);
        if (m_state == 1) {
            servletName = value;
            m_state = 0;
        } else if (m_state == 2) {
            servletNames.put(servletName, value);
            m_state = 0;
        } else if (m_state == 10 || m_state == 20) {
            m_paramName = value;
        } else if (m_state == 11) {
            if (m_paramName == null) {
                System.err.println("Context parameter value '" + value + "' without name");
                System.exit(-1);
            }
            contextParams.put(m_paramName, value);
            m_paramName = null;
            m_state = 0;
        } else if (m_state == 21) {
            if (m_paramName == null) {
                System.err.println("Servlet parameter value '" + value + "' without name");
                System.exit(-1);
            }
            Map<String,String> p = initParams.get(servletName);
            if (p == null) {
                p = new HashMap<>();
                initParams.put(servletName, p);
            }
            p.put(m_paramName, value);
            m_paramName = null;
            m_state = 0;
        }
    }
}
