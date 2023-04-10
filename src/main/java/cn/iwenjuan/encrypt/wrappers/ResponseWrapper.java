package cn.iwenjuan.encrypt.wrappers;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * @author li1244
 * @date 2023/3/29 14:30
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;

    private ServletOutputStream out;

    private PrintWriter writer;

    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        init(response);
    }

    private void init(HttpServletResponse response) throws IOException {
        buffer = new ByteArrayOutputStream();
        out = new OutputStreamWrapper(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer));
    }

    public String getContent() throws IOException {
        flushBuffer();
        return buffer.toString();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    private class OutputStreamWrapper extends ServletOutputStream {

        private ByteArrayOutputStream bos;

        public OutputStreamWrapper(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
            bos = byteArrayOutputStream;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            bos.write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bos.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
