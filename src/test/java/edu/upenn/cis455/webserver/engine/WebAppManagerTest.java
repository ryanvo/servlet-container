package edu.upenn.cis455.webserver.engine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

/**
 * @author Ryan Vo
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class WebAppManagerTest {
    @Mock private ServletConfig mockConfig;

    private ServletContextBuilder contextBuilder;
    private ApplicationContext context;
    @Before public void setup() {

        contextBuilder = new ServletContextBuilder();
        contextBuilder.setName("CalculatorServlet");
        contextBuilder.setRealPath(getClass().getResource("/Servlets/web").getPath());
        contextBuilder.setContextParams(new HashMap<>());
        context = new ApplicationContext(contextBuilder);


    }


    @Test
    public void shouldLaunchServletWhenGivenConfig() throws Exception {

//        given(mockConfig.getServletName()).willReturn("CalculatorServlet");
//        given(mockConfig.getServletContext()).willReturn(context);
//        given(mockConfig.getInitParameterNames()).willReturn(new Vector<>().elements());

        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/Servlets/web/WEB-INF/web.xml").getPath());
        WebAppManager webAppManager = new WebAppManager(webXml, context);

        webXml.parse();
        webAppManager.launch(mockConfig);

    }
}
