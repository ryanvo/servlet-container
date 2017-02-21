package edu.upenn.cis455.webserver.servlet.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ryan Vo
 */
public interface Buffer {
    void write(int i) throws IOException;

    void write(byte[] data) throws IOException;

    void write(byte[] data, int offset, int length) throws IOException;

    void close() throws IOException;

    void flush() throws IOException;

    int size();

    void writeTo(OutputStream out) throws IOException;
}
