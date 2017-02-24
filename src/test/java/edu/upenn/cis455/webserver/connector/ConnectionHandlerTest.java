 package edu.upenn.cis455.webserver.connector;

 import edu.upenn.cis455.webserver.engine.AppContext;
import edu.upenn.cis455.webserver.engine.Container;
import edu.upenn.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis455.webserver.engine.http.HttpResponse;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.*;


/**
 * @author Ryan Vo
 */
public class ConnectionHandlerTest {

    @Test
    public void shouldRequireHostHeaderForHttpVersionAbove1point0() throws Exception {
        Map<String, List<String>> headersWithHost = new HashMap<>();
        String[] values = new String[] {"test101:8080"};
        headersWithHost.put("host", Arrays.asList(values));

        Map<String, List<String>> headersWithoutHost = new HashMap<>();

        Socket mockSocket = mock(Socket.class);
        Container mockContainer = mock(Container.class);
        RequestProcessor mockProcessor = mock(RequestProcessor.class);
        ResponseProcessor mockResponseProcessor = mock(ResponseProcessor.class);

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
        ResponseProcessor mockResponseProcessor = mock(ResponseProcessor.class);

        when(mockHttpRequest.getProtocol()).thenReturn("HTTP/1.1");
        when(mockHttpRequest.getHeaders()).thenReturn(headers);

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
        ResponseProcessor mockResponseProcessor = mock(ResponseProcessor.class);

        OutputStream mockOutputStream = mock(OutputStream.class);
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getProtocol()).thenReturn("HTTP/1.0");
        when(mockHttpRequest.getHeaders()).thenReturn(headers);

    }
}
