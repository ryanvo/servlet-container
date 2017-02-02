package edu.upenn.cis.cis555.webserver;

import edu.upenn.cis.cis455.webserver.engine.WebXmlHandler;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author rtv
 */
public class WebXmlHandlerTest {

    @Test
    public void shouldParseWebAppName() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/resources/testWeb.xml").getPath());

        assertThat(webXml.getWebAppName(), is("Test Web.xml"));
    }

    @Test
    public void shouldParseContextParams() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/resources/testWeb.xml").getPath());

        assertThat(webXml.getContextParams(), hasSize(2));
        assertThat(webXml.getContextParams(), hasItems("contextParamOne"));
        assertThat(webXml.getContextParams(), hasItems("contextParamTwo"));

        assertThat(webXml.getContextParamByKey("contextParamOne"), is("true"));
        assertThat(webXml.getContextParamByKey("contextParamTwo"), is("address@somedomain.com"));
    }

    @Test
    public void shouldParseServletName() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/resources/testWeb.xml").getPath());

        assertThat(webXml.getServletNames(), hasSize(2));
        assertThat(webXml.getServletNames(), hasItems("redteam", "blueteam"));
    }

    @Test
    public void shouldParseServletClass() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/resources/testWeb.xml").getPath());

        assertThat(webXml.getClassByServletName("redteam"), is("edu.upenn.cis.cis555.webserver.WebXmlHandlerTest"));
        assertThat(webXml.getClassByServletName("blueteam"), is("webserver.WebXmlHandlerTest"));

    }

    @Test
    public void shouldParseServletParams() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/resources/testWeb.xml").getPath());

        Map<String, String> initParams1 = webXml.getInitParamsByServletName("redteam");
        assertThat(initParams1.keySet(), hasSize(2));
        assertThat(initParams1.get("teamColor"), is("red"));
        assertThat(initParams1.get("bgColor"), is("#CC0000"));

        Map<String, String> initParams2 = webXml.getInitParamsByServletName("blueteam");
        assertThat(initParams2.keySet(), hasSize(2));
        assertThat(initParams2.get("teamColor"), is("blue"));
        assertThat(initParams2.get("bgColor"), is("#0000CC"));

    }

    @Test
    public void shouldParseServletPatterns() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/resources/testWeb.xml").getPath());

        assertThat(webXml.getPatternByServletName("redteam"), hasSize(1));
        assertThat(webXml.getPatternByServletName("redteam"), hasItem("/red/*"));

        assertThat(webXml.getPatternByServletName("blueteam"), hasSize(2));
        assertThat(webXml.getPatternByServletName("blueteam"), hasItem("/foo/bar"));
        assertThat(webXml.getPatternByServletName("blueteam"), hasItem("/blue/*"));
    }

}