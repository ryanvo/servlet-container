package edu.upenn.cis.cis455.webserver.engine.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author rtv
 */
public class ChunkedOutputStream extends OutputStream{

    private static final byte[] CLRF = new byte[] {'\r', '\n' };
    private static final byte[] TERMINAL = new byte[] { 0 };


    private boolean isOpen = true;

    private OutputStream out;

    public ChunkedOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int i) throws IOException {
        write(new byte[] { (byte) i }, 0, 1);
    }

    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }


    public void write(byte[] data, int offset, int length) throws IOException {
        byte[] chunkSize = Integer.toHexString(length).getBytes();
        out.write(chunkSize);
        out.write(CLRF);
        out.write(data, offset, length);
        out.write(CLRF);

    }

    public void close() throws IOException {
        writeClosingChunk();
        out.flush();
        isOpen = false;
    }

    public void flush() throws IOException {
        out.flush();
    }

//    public void finish() throws IOException {
//        writeClosingChunk();
//        out.flush();
//        isOpen = false;
//    }

    public void writeClosingChunk() throws IOException {
        out.write(TERMINAL);
        out.write(CLRF);
        out.write(CLRF);
    }
}
