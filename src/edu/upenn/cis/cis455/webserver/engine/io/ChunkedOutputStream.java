package edu.upenn.cis.cis455.webserver.engine.io;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Thread.sleep;

/**
 * @author rtv
 */
public class ChunkedOutputStream extends OutputStream {

    private static final byte[] CLRF = new byte[] {'\r', '\n' };
    private static final byte[] TERMINAL = new byte[] { (byte) '0' };

    private boolean isOpen = true;
    private OutputStream out;

    public ChunkedOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int i) throws IOException {
        out.write(new byte[] { (byte) i }, 0, 1);
    }

    public void write(byte[] data) throws IOException {
        out.write(data, 0, data.length);
    }


    public void write(byte[] data, int offset, int length) throws IOException {
        byte[] chunkSize = Integer.toHexString(length).getBytes();
        out.write(chunkSize);
        out.write(CLRF);
        out.write(data, offset, length);
        out.write(CLRF);
    }


    public void print(String s) throws IOException {
        out.write(s.getBytes(), 0, s.length());
    }

    public void println(String s) throws IOException {
        s = s + "\n";
        out.write(s.getBytes(), 0, s.length());
    }

    public void close() throws IOException {
        writeClosingChunk();
        out.flush();
        isOpen = false;
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void writeClosingChunk() throws IOException {
        out.write(TERMINAL);
        out.write(CLRF);
        out.write(CLRF);
    }
}
