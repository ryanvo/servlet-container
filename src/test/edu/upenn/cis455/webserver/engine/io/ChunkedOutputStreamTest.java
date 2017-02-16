package edu.upenn.cis455.webserver.engine.io;

import edu.upenn.cis455.webserver.servlet.io.ChunkedOutputStream;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Ryan Vo
 */
public class ChunkedOutputStreamTest {

    //TODO need to fix these tests with new version

    @Test
    public void shouldWriteChunkToOutputStream() throws Exception {

        final byte[] data = {'f', 'o', 'o'};
        final byte[] CRLF = {'\r', '\n'};
        final byte[] chunkSize = Integer.toHexString(data.length).getBytes();

        OutputStream mockOutputStream = mock(OutputStream.class);

        ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(mockOutputStream);

        chunkedOutputStream.write(data, 0, data.length);

        verify(mockOutputStream).write(chunkSize);
        verify(mockOutputStream, times(2)).write(CRLF);
        verify(mockOutputStream).write(data, 0,  data.length);

    }

    @Test
    public void shouldWriteClosingChunkSequenceOnClose() throws Exception {

        final byte[] CRLF = new byte[] {'\r', '\n' };
        final byte[] TERMINAL = new byte[] { 0 };

        OutputStream mockOutputStream = mock(OutputStream.class);

        ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(mockOutputStream);

        chunkedOutputStream.close();

        InOrder inOrder = inOrder(mockOutputStream);
        inOrder.verify(mockOutputStream).write(TERMINAL);
        inOrder.verify(mockOutputStream, times(2)).write(CRLF);
        inOrder.verify(mockOutputStream).flush();

    }

}
