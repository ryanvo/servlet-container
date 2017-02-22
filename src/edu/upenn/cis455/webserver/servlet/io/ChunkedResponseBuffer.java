package edu.upenn.cis455.webserver.servlet.io;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author rtv
 */
public class ChunkedResponseBuffer extends ServletOutputStream implements Buffer {

    private static final byte[] CRLF = new byte[] {'\r', '\n' };
    private static final byte[] TERMINAL = new byte[] { (byte) '0' };

    private ByteArrayOutputStream out;

    public ChunkedResponseBuffer(int size) {
        this.out = new ByteArrayOutputStream(size);
    }


    private void writeTerminalChunk() throws IOException {
        out.write(TERMINAL);
        out.write(CRLF);
        out.write(CRLF);
    }

    @Override
    public void write(int i) throws IOException {
        out.write(new byte[] { (byte) i }, 0, 1);
    }


    @Override
    public void write(byte[] data) throws IOException {
        out.write(data, 0, data.length);
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        byte[] chunkSize = Integer.toHexString(length).getBytes();
        out.write(chunkSize);
        out.write(CRLF);
        out.write(data, 0, length);
        out.write(CRLF);
    }

    @Override
    public void clear() {
        out.reset();
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

    @Override
    public int size() {
        return out.size();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        this.writeTerminalChunk();
        this.flush();
        this.out.writeTo(out);
    }

    @Override
    public ServletOutputStream toServletOutputStream() {
        return this;
    }

}
