package edu.upenn.cis455.webserver.engine.io;

import edu.upenn.cis455.webserver.engine.http.io.ChunkedResponseBuffer;
import edu.upenn.cis455.webserver.engine.http.io.ResponseBuffer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ryan Vo
 */
public class ChunkedOutputStreamTest {

    @Test
    public void shouldWorkWithInputStream() throws Exception {



        ResponseBuffer responseBuffer = new ResponseBuffer(1024);

        PrintWriter writer = new PrintWriter(responseBuffer);

        writer.write("Wiki");
        writer.flush();

        writer.write("pedia");
        writer.flush();

        writer.write(" in\r\n\r\nchunks.");
        writer.flush();

        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        responseBuffer.writeTo(out);

        byte[] expected = "Wikipedia in\r\n\r\nchunks.".getBytes();
        assertThat(out.toByteArray(), is(expected));
    }

    @Test
    public void shouldWorkWithChunkedInputStream() throws Exception {


        ChunkedResponseBuffer responseBuffer = new ChunkedResponseBuffer(1024);

        PrintWriter writer = new PrintWriter(responseBuffer);

        writer.write("Wiki");
        writer.flush();

        writer.write("pedia");
        writer.flush();

        writer.write(" in\r\n\r\nchunks.");
        writer.flush();

        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        responseBuffer.writeTo(out);

        byte[] expected = "4\r\nWiki\r\n5\r\npedia\r\ne\r\n in\r\n\r\nchunks.\r\n0\r\n\r\n".getBytes();
        assertThat(out.toByteArray(), is(expected));


    }


}
