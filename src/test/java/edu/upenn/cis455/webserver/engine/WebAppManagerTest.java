package edu.upenn.cis455.webserver.engine;

import org.junit.Before;
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

    private AppContextBuilder contextBuilder;
    private AppContext context;
    @Before public void setup() {

        contextBuilder = new AppContextBuilder();
        contextBuilder.setName("CalculatorServlet");
        contextBuilder.setRealPath(getClass().getResource("/Servlets/web").getPath());
        contextBuilder.setContextParams(new HashMap<>());
        context = new AppContext(contextBuilder);


    }
//
//
//    @Test
//    public void shouldLaunchServletWhenGivenConfig() throws Exception {
//
////        given(mockConfig.getServletName()).willReturn("CalculatorServlet");
////        given(mockConfig.getServletContext()).willReturn(context);
////        given(mockConfig.getInitParameterNames()).willReturn(new Vector<>().elements());
//
//        WebXmlHandler webXml = new WebXmlHandler(getClass().getResource("/Servlets/web/WEB-INF/web.xml").getPath());
//        WebApp webAppManager = new WebApp(webXml);
//
//        webXml.parse();
//        webAppManager.launch(mockConfig);
//
//    }
}
