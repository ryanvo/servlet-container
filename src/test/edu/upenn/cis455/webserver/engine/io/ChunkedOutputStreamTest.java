package edu.upenn.cis455.webserver.engine.io;

import edu.upenn.cis455.webserver.servlet.io.ResponseBuffer;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Spy;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class ChunkedOutputStreamTest {

    //TODO need to fix these tests with new version

//    @Test
//    public void shouldWriteChunkToOutputStream() throws Exception {
//
//        final byte[] data = {'f', 'o', 'o'};
//        final byte[] CRLF = {'\r', '\n'};
//        final byte[] chunkSize = Integer.toHexString(data.length).getBytes();
//
//
//        ResponseBuffer responseBuffer = new ResponseBuffer(64);
//        ResponseBuffer spyResponseBuffer = spy(responseBuffer);
//
//
//        responseBuffer.write(data, 0, data.length);
//
//        verify(spyResponseBuffer).write(chunkSize);
//        verify(spyResponseBuffer, times(2)).write(CRLF);
//        verify(spyResponseBuffer).write(data, 0,  data.length);

//    }

//    @Test
//    public void shouldWriteClosingChunkSequenceOnClose() throws Exception {
//
//        final byte[] CRLF = new byte[] {'\r', '\n' };
//        final byte[] TERMINAL = new byte[] { 0 };
//
//        ByteArrayOutputStream mockOutputStream = mock(ByteArrayOutputStream.class);
//        ResponseBuffer responseBuffer = new ResponseBuffer(mockOutputStream);
//
//        responseBuffer.close();
//
//        InOrder inOrder = inOrder(mockOutputStream);
//        inOrder.verify(mockOutputStream).write(TERMINAL);
//        inOrder.verify(mockOutputStream, times(2)).write(CRLF);
//        inOrder.verify(mockOutputStream).flush();
//
//    }

    @Test
    public void shouldWorkWithInputStream() throws Exception {



        ResponseBuffer responseBuffer = new ResponseBuffer(1024);

        PrintWriter writer = new PrintWriter(responseBuffer);

        writer.write("test");

        writer.flush();


        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        responseBuffer.writeTo(out);

        assertThat(out.size(), is(4));


    }



}
