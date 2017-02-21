package edu.upenn.cis455.webserver.servlet.io;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Thread.sleep;

/**
 * @author rtv
 */
public class ResponseBuffer extends ServletOutputStream implements Buffer {

    private ByteArrayOutputStream out;

    public ResponseBuffer(int size) {
        this.out = new ByteArrayOutputStream(size);
    }


    @Override
    public void write(int i) throws IOException {
        write(new byte[] { (byte) i }, 0, 1);
    }


    @Override
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    public void writeTo(OutputStream out) throws IOException {
        this.flush();
        this.out.writeTo(out);
    }

    public int size() {
        return out.size();
    }
    @Override
    public void close() throws IOException {
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


}
