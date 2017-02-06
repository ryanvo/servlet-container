package edu.upenn.cis.cis455.webserver.engine.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * @author rtv
 */
public class ChunkedPrintWriter extends Writer {


    private OutputStream out;

    public ChunkedPrintWriter(OutputStream out) {
        this.out = out;
    }


    public void print(String s) throws IOException {
        write(s);
    }

    public void println(String s) throws IOException {
        write(s + "\n");
    }

    @Override
    public void write(String s) throws IOException {
        out.write(s.getBytes());
        out.flush();
    }

    @Override
    public void write(char[] chars, int i, int i1) throws IOException {
        out.write(new String(chars).getBytes(StandardCharsets.UTF_8), 0, chars.length);
        out.flush();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
