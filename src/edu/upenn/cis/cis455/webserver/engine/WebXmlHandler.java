package edu.upenn.cis.cis455.webserver.engine;

import org.apache.http.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author rtv
 */
public class WebXmlHandler extends DefaultHandler {

    private static Logger log  = LogManager.getLogger(WebXmlHandler.class);

    private Map<String, String> servletClassByName = new HashMap<>();
    private Map<String, Map<String, String>> initParams = new HashMap<>();
    private Map<String, String> contextParams = new HashMap<>();
    private Map<String, String> servletNameByPattern = new HashMap<>();

    //    private Map<String, Set<String>> servletPatternByName = new HashMap<>();

    private String webAppName;
    private String servletName;
    private String servletClass;
    private String servletPattern;
    private String paramName;
    private String paramValue;
    private String webXmlPath;

    private StringBuilder buffer = new StringBuilder();

    public WebXmlHandler(String webXmlPath) {
        this.webXmlPath = webXmlPath;
    }

    public void parse() throws IOException, ParseException {

        log.debug("Opening web.xml file: " + webXmlPath);
        try {
            File file = new File(webXmlPath);
            if (!file.exists() || !file.canRead()) {
                throw new IOException();
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(file, this);

        } catch (ParserConfigurationException | SAXException e) {
            throw new ParseException();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        /* Reset the buffer between elements */
        buffer.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        switch (qName) {
            case "display-name":
                webAppName = buffer.toString().trim();
                break;

            case "servlet-name":
                servletName = buffer.toString().trim();
                break;

            case "servlet-class":
                servletClass = buffer.toString().trim();
                break;

            case "param-name":
                paramName = buffer.toString().trim();
                break;

            case "param-value":
                paramValue = buffer.toString().trim();
                break;

            case "url-pattern":
                servletPattern = buffer.toString().trim();
                break;

            case "context-param":
                contextParams.put(paramName, paramValue);
                break;

            case "init-param":
                initParams.putIfAbsent(servletName, new HashMap<>());
                Map<String, String> servletInitParams = initParams.get(servletName);
                servletInitParams.put(paramName, paramValue);
                break;

            case "servlet-mapping":
                servletNameByPattern.put(servletPattern, servletName);

//                servletPatternByName.putIfAbsent(servletName, new HashSet<>());
//                Set<String> patterns = servletPatternByName.get(servletName);
//                patterns.add(servletPattern);

                break;

            case "servlet":
                servletClassByName.put(servletName, servletClass);
                break;

            default:
                log.debug("Unmapped qname: " + qName);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        log.error(e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        log.error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        log.error(e);
    }

    public Map<String, String> getContextParams() {
        return contextParams;
    }

    public String getWebAppName() {
        return webAppName;
    }

    public Set<String> getServletNames() {
        return servletClassByName.keySet();
    }

    public String getClassByServletName(String name) {
        return servletClassByName.get(name);
    }

    public Map<String, String> getServletInitParamsByName(String name) {
        return initParams.get(name);
    }

    public Map<String, String> getNameByPatterns() {
        return servletNameByPattern;
    }

}
