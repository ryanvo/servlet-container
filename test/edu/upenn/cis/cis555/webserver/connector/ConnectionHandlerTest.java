package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.ConnectionHandler;
import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import edu.upenn.cis.cis455.webserver.connector.RequestProcessor;
import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.Request;

import java.net.Socket;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;


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

        ConnectionHandler connectionHandler = new ConnectionHandler(mockSocket, mockContainer, mockProcessor);

        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.0", headersWithHost), is(true));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.1", headersWithHost), is(true));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.0", headersWithoutHost), is(true));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/1.1", headersWithoutHost), is(false));
        assertThat(connectionHandler.hasValidHostHeader("HTTP/0.9", headersWithoutHost), is(true));
    }



}
