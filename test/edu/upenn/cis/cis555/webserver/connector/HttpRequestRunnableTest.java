package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.HttpRequestRunnable;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ryan Vo
 */
public class HttpRequestRunnableTest {

    @Test
    public void shouldPopulateRequestWithStatusLineArguments() throws Exception{
        HttpRequest request = new HttpRequest();
        Runnable r1 = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8081);
                Socket socket = serverSocket.accept();
                HttpRequestRunnable requestRunnable = new HttpRequestRunnable(socket, null);
                requestRunnable.createRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };


        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPath("/search")
                .setPort(8081)
                .build();
        Runnable r2 = () -> {
            try {

                HttpGet httpget = new HttpGet(uri);
                CloseableHttpClient httpclient = HttpClients.createDefault();
                httpclient.execute(httpget);
                httpclient.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
        sleep(1000);

        assertThat(request.getType(), is("GET"));
        assertThat(request.getRequestURI().toString(), is("/search"));
    }

}
