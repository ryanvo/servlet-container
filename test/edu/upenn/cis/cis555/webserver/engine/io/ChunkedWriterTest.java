package edu.upenn.cis.cis555.webserver.engine.io;

import edu.upenn.cis.cis455.webserver.servlet.io.ChunkedOutputStream;
import edu.upenn.cis.cis455.webserver.servlet.io.ChunkedWriter;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ryan Vo
 */
public class ChunkedWriterTest {

    @Test
    public void shouldChunkEncodeWhenWriteIsInvoked() throws Exception {

        final String CRLF = "\r\n";
        final String str1 = "foobar";
        final String str2 = "foo";
        final String str1LenHex = Integer.toHexString(str1.length());
        final String str2LenHex = Integer.toHexString(str2.length());

        ByteArrayOutputStream streamData = new ByteArrayOutputStream();

        ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(streamData);
        ChunkedWriter chunkedWriter = new ChunkedWriter(chunkedOutputStream);

        chunkedWriter.write(str1);
        chunkedWriter.write(str2);
        chunkedWriter.flush();

        final String expectedStreamData = str1LenHex + CRLF + str1 + CRLF + str2LenHex + CRLF + str2 + CRLF;


        assertThat(streamData.toByteArray(), is(expectedStreamData.getBytes()));
    }

    @Test
    public void shouldPrintLnWithChunkedEncoding() throws Exception {

        final String CRLF = "\r\n";
        final String str1 = "foobar";
        final String str2 = "foo";
        final String str1LenHex = Integer.toHexString(str1.length() + 1);
        final String str2LenHex = Integer.toHexString(str2.length() + 1);

        ByteArrayOutputStream streamData = new ByteArrayOutputStream();

        ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(streamData);
        ChunkedWriter chunkedWriter = new ChunkedWriter(chunkedOutputStream);

        chunkedWriter.write(str1);
        chunkedWriter.write(str2);
        chunkedWriter.flush();

        final String expectedStreamData = str1LenHex + CRLF + str1 + '\n' + CRLF + str2LenHex + CRLF + str2 + '\n' + CRLF;

        assertThat(streamData.toByteArray(), is(expectedStreamData.getBytes()));
    }

    @Test
    public void shouldAppendTerminalChunkWhenCloseInvoked() throws Exception {

        final String CRLF = "\r\n";
        final String str2 = "foo";
        final String str2LenHex = Integer.toHexString(str2.length() + 1);
        final String terminalChunk = '0' + CRLF + CRLF;

        ByteArrayOutputStream streamData = new ByteArrayOutputStream();

        ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(streamData);
        ChunkedWriter chunkedWriter = new ChunkedWriter(chunkedOutputStream);

        chunkedWriter.write(str2);
        chunkedWriter.flush();
        chunkedWriter.close();

        final String expectedStreamData = str2LenHex + CRLF + str2 + '\n' + CRLF + terminalChunk;

        assertThat(streamData.toByteArray(), is(expectedStreamData.getBytes()));

    }

}
