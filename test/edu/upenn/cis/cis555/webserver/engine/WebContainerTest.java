package edu.upenn.cis.cis555.webserver.engine;

import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestRunnable;
import edu.upenn.cis.cis455.webserver.engine.*;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.engine.http.HttpServlet;
import edu.upenn.cis.cis455.webserver.thread.WorkerPool;
import org.junit.Test;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class WebContainerTest {

    @Test
    public void shouldCallDoGetWithGetRequest() throws Exception {

        HttpServlet mockServlet = mock(HttpServlet.class);
        ServletManager mockServletManager = mock(ServletManager.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpRequest mockRequest = mock(HttpRequest.class);
        when(mockServletManager.match("/test")).thenReturn(mockServlet);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getRequestURI()).thenReturn("/test");

        WebContainer webContainer = new WebContainer(mockServletManager);
        webContainer.dispatch(mockRequest, mockResponse);

        verify(mockServletManager).match("/test");
        verify(mockServlet).doGet(mockRequest, mockResponse);

    }

    @Test
    public void shouldReturnContextProvidedByServletManager() throws Exception {

        ServletContext mockServletContext = mock(ServletContext.class);
        ServletManager mockServletManager = mock(ServletManager.class);
        when(mockServletManager.getContext()).thenReturn(mockServletContext);

        WebContainer webContainer = new WebContainer(mockServletManager);
        ServletContext contextInContainer = webContainer.getContext();

        assertThat(contextInContainer, is(mockServletContext));

    }


}
