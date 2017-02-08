package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Ryan Vo
 */
public class HttpProcessorTest {

    @Test
    public void shouldParseHeadersWithLowercaseKeyAndCaseSensitiveValue() throws Exception {

        List<String> headerFromRequest = new ArrayList<>();
        
        headerFromRequest.add("Accept-Language: en-us");
        headerFromRequest.add("Accept-Encoding: gzip, deflate");
        headerFromRequest.add("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        headerFromRequest.add("Host: test101:8080");
        headerFromRequest.add("Connection: Keep-Alive");

        HttpRequestProcessor processor = new HttpRequestProcessor();

        Map<String, List<String>> parsedHeaders = processor.parseHeaders(headerFromRequest);



        assertThat(parsedHeaders.get("accept-language"), containsInAnyOrder("en-us"));
        assertThat(parsedHeaders.get("accept-encoding"), containsInAnyOrder("gzip", "deflate"));
        assertThat(parsedHeaders.get("user-agent"), containsInAnyOrder("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"));
        assertThat(parsedHeaders.get("host"), containsInAnyOrder("test101:8080"));
        assertThat(parsedHeaders.get("connection"), containsInAnyOrder("Keep-Alive"));

    }

    @Test
    public void shouldParseHeadersWithValuesThatSpanMultipleLines() throws Exception {

        List<String> headerFromRequest = new ArrayList<>();

        headerFromRequest.add("Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg,");
        headerFromRequest.add("  application/x-shockwave-flash, application/vnd.ms-excel,");
        headerFromRequest.add("\tapplication/vnd.ms-powerpoint, application/msword, */*");
        headerFromRequest.add("Accept-Language: en-us");
        headerFromRequest.add("Host: test101:8080");


        HttpRequestProcessor processor = new HttpRequestProcessor();

        Map<String, List<String>> parsedHeaders = processor.parseHeaders(headerFromRequest);


        assertThat(parsedHeaders.get("accept-language"), containsInAnyOrder("en-us"));
        assertThat(parsedHeaders.get("host"), containsInAnyOrder("test101:8080"));
        assertThat(parsedHeaders.get("accept"), containsInAnyOrder("image/gif", "image/x-xbitmap", "image/jpeg",
                "image/pjpeg", "application/x-shockwave-flash", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/msword", "*/*"));

    }

}
