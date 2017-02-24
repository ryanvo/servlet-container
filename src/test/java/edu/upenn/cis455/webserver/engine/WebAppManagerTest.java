package edu.upenn.cis455.webserver.engine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Vector;

import static org.mockito.BDDMockito.given;

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
        contextBuilder.setContextParams(new HashMap<>());
        context = new AppContext(contextBuilder);
    }


    @Test
    public void shouldLaunchServletWhenGivenConfig() throws Exception {

        WebXmlHandler webXml = new WebXmlHandler("");
        WebApp webAppManager = new WebApp(context);


    }
}
