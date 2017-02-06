package edu.upenn.cis.cis455.webserver.servlet.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author rtv
 */
public class ChunkedWriter extends Writer {

    ChunkedOutputStream out;

    public ChunkedWriter(OutputStream out) {
        this.out = new ChunkedOutputStream(out);
    }

    public void unchunkedPrint(String s) throws IOException {
        out.unchunkedWrite(s);
    }

    public void unchunkedPrintLn(String s) throws IOException {
        out.unchunkedWrite(s + "\n");
    }

    public void unchunkedWrite(byte[] b, int i, int i1) throws IOException {
        out.unchunkedWrite(b, i, i1);
    }

    public void finish() throws IOException {
        out.writeTerminalChunk();
    }


    @Override
    public void write(int i) throws IOException {
        out.write(i);
    }

    @Override
    public void write(char[] chars) throws IOException {
        out.write(new String(chars).getBytes());
    }

    @Override
    public void write(String s) throws IOException {
        out.write(s.getBytes());
    }

    @Override
    public void write(String s, int i, int i1) throws IOException {
        out.write(s.substring(i, i1).getBytes());
    }

    @Override
    public void write(char[] chars, int i, int i1) throws IOException {
        out.write(new String(chars).substring(i, i1).getBytes());
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        finish();
    }



//    public void println(String s) throws IOException {
//        out.println(s);
//    }
//
//    public void write(byte[] b) throws IOException {
//        out.write(b);
//    }

//    @Override
//    public void write(String s) {
//        out.write(s.getBytes(), 0, s.length());
//        out.flush();
//    }



//    @Override
//    public void write(char[] chars, int i, int i1) throws IOException {
//        out.write(new String(chars).getBytes(StandardCharsets.UTF_8), 0, chars.length);
//        out.flush();
//    }

//    @Override
//    public void flush() throws IOException {
//        out.flush();
//    }

//    @Override
//    public void close() throws IOException {
//        out.close();
//    }
}
