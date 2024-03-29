package edu.upenn.cis455.webserver.engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


/**
 * @author rtv
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class WebXmlHandlerTest {

    private String testWebXmlFile = "/files/testWeb.xml";

    @Test
    public void shouldParseWebAppName() throws Exception {

        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource(testWebXmlFile).getPath());

        webXml.parse();

        assertThat(webXml.getWebAppName(), is("Test Web.xml"));
    }

    @Test
    public void shouldParseContextParams() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource(testWebXmlFile).getPath());

        webXml.parse();

        assertThat(webXml.getContextParams().keySet(), hasSize(2));
        assertThat(webXml.getContextParams().keySet(), hasItems("contextParamOne"));
        assertThat(webXml.getContextParams().keySet(), hasItems("contextParamTwo"));

        assertThat(webXml.getContextParams().get("contextParamOne"), is("true"));
        assertThat(webXml.getContextParams().get("contextParamTwo"), is("address@somedomain.com"));
    }



    @Test
    public void shouldParseServletName() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource(testWebXmlFile).getPath());

        webXml.parse();

        assertThat(webXml.getServletNames(), hasSize(2));
        assertThat(webXml.getServletNames(), hasItems("redteam", "blueteam"));
    }

    @Test
    public void shouldParseServletClass() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource(testWebXmlFile).getPath());

        webXml.parse();

        assertThat(webXml.getClassByServletName("redteam"), is("edu.upenn.cis.cis555.webserver.WebXmlHandlerTest"));
        assertThat(webXml.getClassByServletName("blueteam"), is("webserver.WebXmlHandlerTest"));

    }

    @Test
    public void shouldParseServletParams() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource(testWebXmlFile).getPath());

        webXml.parse();

        Map<String, String> initParams1 = webXml.getServletInitParamsByName("redteam");
        assertThat(initParams1.keySet(), hasSize(2));
        assertThat(initParams1.get("teamColor"), is("red"));
        assertThat(initParams1.get("bgColor"), is("#CC0000"));

        Map<String, String> initParams2 = webXml.getServletInitParamsByName("blueteam");
        assertThat(initParams2.keySet(), hasSize(2));
        assertThat(initParams2.get("teamColor"), is("blue"));
        assertThat(initParams2.get("bgColor"), is("#0000CC"));

    }

    @Test
    public void shouldParseServletPatterns() throws Exception {
        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource(testWebXmlFile).getPath());

        webXml.parse();

        assertThat(webXml.getPatternsByName().get("blueteam"), hasItems("/blue/*", "/foo/bar"));
        assertThat(webXml.getPatternsByName().get("redteam"), hasItems("/red/*"));
    }

}
