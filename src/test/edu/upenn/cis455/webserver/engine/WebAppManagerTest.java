package edu.upenn.cis455.webserver.engine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Vector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

/**
 * @author Ryan Vo
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class WebAppManagerTest {

    @Mock private ServletContextBuilder mockContextBuilder;
//    @Mock private ServletConfigBuilder mockConfigBuilder;
    @Mock private ServletConfig mockConfig;

    private ServletContext context;
    @Before public void setup() {

        given(mockContextBuilder.getContextParams()).willReturn(new HashMap<>());
        given(mockContextBuilder.getRealPath()).willReturn(getClass().getResource("/webapps/calculator").getPath());

        context = new ServletContext(mockContextBuilder);


    }


    @Test
    public void shouldLaunchServletWhenGivenConfig() throws Exception {

        given(mockConfig.getServletName()).willReturn("CalculatorServlet");
        given(mockConfig.getServletContext()).willReturn(context);
        given(mockConfig.getInitParameterNames()).willReturn(new Vector<>().elements());

        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/WEB-INF/web.xml").getPath());
        WebAppManager webAppManager = new WebAppManager(webXml, context);

        webXml.parse();
        webAppManager.launch(mockConfig);

    }
}
