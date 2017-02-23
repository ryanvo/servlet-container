package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis455.webserver.engine.http.HttpResponse;
import org.junit.Test;

import javax.servlet.http.HttpServlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ryan Vo
 */
public class WebAppContainerTest {

    @Test
    public void shouldCallServiceWithRequest() throws Exception {

        HttpServlet mockServlet = mock(HttpServlet.class);
        WebApp mockWebAppManager = mock(WebApp.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpRequest mockRequest = mock(HttpRequest.class);
        SessionManager mockSessionManager = mock(SessionManager.class);
        when(mockWebAppManager.match("/test")).thenReturn(mockServlet);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getRequestURI()).thenReturn("/test");

        WebAppContainer webAppContainer = new WebAppContainer(mockWebAppManager, mockSessionManager);
        webAppContainer.dispatch(mockRequest, mockResponse);

        verify(mockWebAppManager).match("/test");
        verify(mockServlet).service(mockRequest, mockResponse);

    }

    @Test
    public void shouldReturnContextProvidedByServletManager() throws Exception {

        AppContext mockAppContext = mock(AppContext.class);
        WebApp mockWebAppManager = mock(WebApp.class);
        SessionManager mockSessionManager = mock(SessionManager.class);

        when(mockWebAppManager.getContext()).thenReturn(mockAppContext);

        WebAppContainer webAppContainer = new WebAppContainer(mockWebAppManager, mockSessionManager);
        AppContext contextInContainer = webAppContainer.getContext("webapp");

        assertThat(contextInContainer, is(mockAppContext));

    }


}
