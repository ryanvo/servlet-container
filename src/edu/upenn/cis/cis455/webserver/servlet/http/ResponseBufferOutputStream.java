package edu.upenn.cis.cis455.webserver.servlet.http;

import org.apache.tools.ant.taskdefs.condition.Socket;

import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * @author Ryan Vo
 */
public class ResponseBufferOutputStream extends ServletOutputStream {

    public BufferedOutputStream out;

    public ResponseBufferOutputStream(int size) {
        this.out = new BufferedOutputStream(new ByteArrayOutputStream(size));
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }
}
