package edu.upenn.cis455.webserver.servlet.io;

import org.apache.tools.ant.taskdefs.condition.Socket;

import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * @author Ryan Vo
 */
public class ResponseBufferOutputStream extends ServletOutputStream {

    private BufferedOutputStream out;
    private ByteArrayOutputStream buffer;


    public ResponseBufferOutputStream(int size) {
        this.buffer = new ByteArrayOutputStream(size);
        this.out = new BufferedOutputStream(this.buffer);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    public void writeTo(OutputStream out) throws IOException {
        this.out.flush();
        this.buffer.writeTo(out);
    }

    public int size() {
        return buffer.size();
    }

}
