 package edu.upenn.cis455.webserver.connector;

 import edu.upenn.cis455.webserver.engine.Container;
 import edu.upenn.cis455.webserver.engine.ServletContext;
 import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
 import edu.upenn.cis455.webserver.servlet.http.HttpResponse;
 import org.junit.Test;
 import org.mockito.InOrder;

 import java.io.OutputStream;
 import java.net.Socket;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import static org.hamcrest.MatcherAssert.assertThat;
 import static org.hamcrest.Matchers.is;
 import static org.mockito.Mockito.*;


/**
 * @author Ryan Vo
 */
public class ConnectionHandlerTest {

    @Test
    public void shouldInvokeTheHelperMethods() throws Exception {

        Socket mockSocket = mock(Socket.class);
        Container mockContainer = mock(Container.class);
        RequestProcessor mockProcessor = mock(RequestProcessor.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockContainer.getContext(any())).thenReturn(mockServletContext);
        when(mockServletContext.getAttribute(any())).thenReturn(mockConnectionManager);

        ConnectionHandler connectionHandler = new ConnectionHandler(mockSocket, mockContainer, mockProcessor);
        ConnectionHandler spyConnectionHandler = spy(connectionHandler);
        doReturn(true).when(spyConnectionHandler).hasValidHostHeader(any(), any());
        doNothing().when(spyConnectionHandler).handle100ContinueRequest(any(), any());
        spyConnectionHandler.run();

        InOrder inOrder = inOrder(mockProcessor, mockConnectionManager);

        inOrder.verify(mockProcessor).process(isA(HttpRequest.class));
        inOrder.verify(mockConnectionManager).update(any(), any());
        verify(spyConnectionHandler).handle100ContinueRequest(any(), any());
        verify(spyConnectionHandler).hasValidHostHeader(any(), any());
        verify(mockContainer).dispatch(isA(HttpRequest.class), isA(HttpResponse.class));
        verify(mockSocket).close();
    }

    @Test
    public void shouldRequireHostHeaderForHttpVersionAbove1point0() throws Exception {
        Map<String, List<String>> headersWithHost = new HashMap<>();
        String[] values = new String[] {"test101:8080"};
        headersWithHost.put("host", Arrays.asList(values));

        Map<String, List<String>> headersWithoutHost = new HashMap<>();

        Socket mockSocket = mock(Socket.class);
        Container mockContainer = mock(Container.class);
        RequestProcessor mockProcessor = mock(RequestProcessor.class);

        ConnectionHandler connectionHandler = new ConnectionHandler(mockSocket, mockContainer, mockProcessor);

        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.0", headersWithHost), is(true));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.1", headersWithHost), is(true));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.0", headersWithoutHost), is(true));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.1", headersWithoutHost), is(false));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/0.9", headersWithoutHost), is(true));
    }

    @Test
    public void shouldSend100ContinueResponseToHttp1point1Clients() throws Exception {
        final byte[] httpContinueResponse = "HTTP/1.1 100 Continue\r\n".getBytes();

        Map<String, List<String>> headers = new HashMap<>();
        String[] expectValue = {"100-continue"};
        headers.put("expect", Arrays.asList(expectValue));

        Socket mockSocket = mock(Socket.class);
        Container mockContainer = mock(Container.class);
        RequestProcessor mockProcessor = mock(RequestProcessor.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getProtocol()).thenReturn("HTTP/1.1");
        when(mockHttpRequest.getHeaders()).thenReturn(headers);

        ConnectionHandler connectionHandler = new ConnectionHandler(mockSocket, mockContainer, mockProcessor);
        connectionHandler.handle100ContinueRequest(mockHttpRequest, mockOutputStream);

        verify(mockOutputStream).write(httpContinueResponse);
//        verify(mockOutputStream).flush();

    }

    @Test
    public void shouldNotSend100ContinueResponseToHttp1point0Clients() throws Exception {
        final byte[] httpContinueResponse = "HTTP/1.1 100 Continue\r\n".getBytes();

        Map<String, List<String>> headers = new HashMap<>();
        String[] expectValue = {"100-continue"};
        headers.put("expect", Arrays.asList(expectValue));

        Socket mockSocket = mock(Socket.class);
        Container mockContainer = mock(Container.class);
        RequestProcessor mockProcessor = mock(RequestProcessor.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getProtocol()).thenReturn("HTTP/1.0");
        when(mockHttpRequest.getHeaders()).thenReturn(headers);

        ConnectionHandler connectionHandler = new ConnectionHandler(mockSocket, mockContainer, mockProcessor);
        connectionHandler.handle100ContinueRequest(mockHttpRequest, mockOutputStream);

        verify(mockOutputStream, never()).write(httpContinueResponse);
        verify(mockOutputStream, never()).flush();

    }
}
