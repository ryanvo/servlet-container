package edu.upenn.cis.cis455.webserver.servlet.io;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Thread.sleep;

/**
 * @author rtv
 */
public class ChunkedOutputStream extends OutputStream {

    private static final byte[] CRLF = new byte[] {'\r', '\n' };
    private static final byte[] TERMINAL = new byte[] { (byte) '0' };

    private OutputStream out;

    public ChunkedOutputStream(OutputStream out) {
        this.out = out;
    }

    public void unchunkedWrite(String s) throws IOException {
        out.write(s.getBytes());
    }

    public void unchunkedWrite(byte[] b, int i, int i1) throws IOException {
        out.write(b, i, i1);
    }

    public void writeTerminalChunk() throws IOException {
        out.write(TERMINAL);
        out.write(CRLF);
        out.write(CRLF);
    }

    @Override
    public void write(int i) throws IOException {
        write(new byte[] { (byte) i }, 0, 1);
    }


    @Override
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        byte[] chunkSize = Integer.toHexString(data.length).getBytes();
        out.write(chunkSize);
        out.write(CRLF);
        out.write(data, 0, data.length);
        out.write(CRLF);
    }

    @Override
    public void close() throws IOException {
        writeTerminalChunk();
        out.flush();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

}
