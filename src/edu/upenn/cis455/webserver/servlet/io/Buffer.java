package edu.upenn.cis455.webserver.servlet.io;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ryan Vo
 */
public interface Buffer {

    int size();

    void writeTo(OutputStream out) throws IOException;

    ServletOutputStream toServletOutputStream();
}
