package edu.upenn.cis.cis555.webserver;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author rtv
 */
public class HttpTestHelper {

    public static Thread sendGet(String host, String path, int port) throws URISyntaxException{

        URI uri = new URIBuilder().setScheme("http")
                                  .setHost(host)
                                  .setPath(path)
                                  .setPort(port)
                                  .build();

        Runnable req = () -> {
            try {

                HttpGet httpget = new HttpGet(uri);
                CloseableHttpClient httpclient = HttpClients.createDefault();
                httpclient.execute(httpget);

                httpclient.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        return new Thread(req);

    }

}
