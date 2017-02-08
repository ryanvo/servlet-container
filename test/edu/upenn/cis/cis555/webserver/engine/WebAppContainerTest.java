package edu.upenn.cis.cis555.webserver.engine;

import edu.upenn.cis.cis455.webserver.engine.*;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class WebAppContainerTest {

    @Test
    public void shouldCallServiceWithRequest() throws Exception {

        HttpServlet mockServlet = mock(HttpServlet.class);
        WebAppManager mockWebAppManager = mock(WebAppManager.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpRequest mockRequest = mock(HttpRequest.class);
        when(mockWebAppManager.match("/test")).thenReturn(mockServlet);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getRequestURI()).thenReturn("/test");

        WebAppContainer webAppContainer = new WebAppContainer(mockWebAppManager);
        webAppContainer.dispatch(mockRequest, mockResponse);

        verify(mockWebAppManager).match("/test");
        verify(mockServlet).service(mockRequest, mockResponse);

    }

    @Test
    public void shouldReturnContextProvidedByServletManager() throws Exception {

        ServletContext mockServletContext = mock(ServletContext.class);
        WebAppManager mockWebAppManager = mock(WebAppManager.class);
        when(mockWebAppManager.getContext()).thenReturn(mockServletContext);

        WebAppContainer webAppContainer = new WebAppContainer(mockWebAppManager);
        ServletContext contextInContainer = webAppContainer.getContext("webapp");

        assertThat(contextInContainer, is(mockServletContext));

    }


}
