package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.HttpRequestRunnable;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis555.webserver.HttpTestHelper;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ryan Vo
 */
public class HttpRequestRunnableTest {

    @Test
    public void shouldPopulateRequestWithStatusLineArguments() throws Exception{

        final int port = 9090;
        final String host = "localhost";
        final String path = "/test";

        HttpRequest request = new HttpRequest();
        Runnable r1 = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();
                HttpRequestRunnable requestRunnable = new HttpRequestRunnable(socket, null);
                requestRunnable.createRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(r1);
        Thread t2 = HttpTestHelper.sendGet(host, path, port);
        t1.start();
        t2.start();
        sleep(1000);

        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getRequestURI(), is("/test"));
    }

}
