package edu.upenn.cis.cis555.webserver.connector;

import edu.upenn.cis.cis455.webserver.connector.HttpRequestProcessor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Ryan Vo
 */
public class HttpProcessorTest {

    @Test
    public void shouldParseHeaders() throws Exception {

        List<String> headerFromRequest = new ArrayList<>();

//        headerFromRequest.add("Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg,");
//        headerFromRequest.add("  application/x-shockwave-flash, application/vnd.ms-excel,");
//        headerFromRequest.add("\tapplication/vnd.ms-powerpoint, application/msword, */*");
        headerFromRequest.add("Accept-Language: en-us");
        headerFromRequest.add("Accept-Encoding: gzip, deflate");
//        headerFromRequest.add("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
//        headerFromRequest.add("Accept-Language: en-us");
//        headerFromRequest.add("Host: test101:8080");
//        headerFromRequest.add("Connection: Keep-Alive");

        HttpRequestProcessor processor = new HttpRequestProcessor();

        Map<String, List<String>> parsedHeaders = processor.parseHeaders(headerFromRequest);

//        assertThat(parsedHeaders, hasEntry("Accept", hasItems("image/gif", "image/x-xbitmap", "image/jpeg",
//                "image/pjpeg")));

        assertThat(parsedHeaders, hasKey("Accept-Language"));
        assertThat(parsedHeaders.get("Accept-Language"), hasItems("en-us"));

        assertThat(parsedHeaders, hasKey("Accept-Encoding"));
        assertThat(parsedHeaders.get("Accept-Encoding"), allOf(hasItems("gzip"), hasItem("deflate"), hasSize(2)));




    }
}
