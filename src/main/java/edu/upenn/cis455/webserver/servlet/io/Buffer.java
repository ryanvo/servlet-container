package edu.upenn.cis455.webserver.servlet.io;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ryan Vo
 */
public interface Buffer {

    int size();

    void clear();

    void writeTo(OutputStream out) throws IOException;

    ServletOutputStream toServletOutputStream();
    OutputStream toOutputStream();
}
