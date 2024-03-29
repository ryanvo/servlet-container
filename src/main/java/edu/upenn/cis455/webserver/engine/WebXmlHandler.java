package edu.upenn.cis455.webserver.engine;


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
 * Parses the web xml file and provides indexed access to
 * relevant fields
 * @author rtv
 */
public class WebXmlHandler extends DefaultHandler {

    private static Logger log  = LogManager.getLogger(WebXmlHandler.class);

    private Map<String, String> servletClassByName = new HashMap<>();
    private Map<String, Map<String, String>> initParams = new HashMap<>();
    private Map<String, String> contextParams = new HashMap<>();
    private Map<String, List<String>> patternsByName = new HashMap<>();

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


    /**
     * Calling this method will begin the parsing operation. The constructor does
     * not start any parsing.
     * @throws IOException
     * @throws SAXException
     */
    public void parse() throws IOException, SAXException {

        log.debug("Opening web.xml file: " + webXmlPath);
        try {
            File file = new File(webXmlPath);
            if (!file.exists() || !file.canRead()) {
                throw new IOException();
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(file, this);

        } catch (ParserConfigurationException|SAXException e) {
            throw new SAXException();
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        /* Reset the chunkedBuffer between elements */
        buffer.setLength(0);
    }


    /**
     * Set fields based on the end tags
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
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

                List<String> patterns = patternsByName.getOrDefault(servletName, new ArrayList<>());
                patterns.add(servletPattern);
                patternsByName.putIfAbsent(servletName, patterns);
                log.info(String.format("WebXmlHandler found servletName:%s servletPattern:%s", servletName,
                        servletPattern));
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

    public String getWebXmlPath() {
        return webXmlPath;
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

    public Map<String, List<String>> getPatternsByName() {
        return patternsByName;
    }

}
