package com.mjanglin.httpserver.core;

import java.io.*;

public class DefaultHttpResponseWriter implements HttpResponseWriter {
    private final BufferedWriter writer;
    private final OutputStream outputStream;

    public DefaultHttpResponseWriter(OutputStream out) {
        this.outputStream = out;
        this.writer = new BufferedWriter(new OutputStreamWriter(out));
    }

    @Override
    public void write(String content) throws IOException {
        writer.write(content);
    }

    @Override
    public void write(byte[] content) throws IOException {
        outputStream.write(content);
    }

    @Override
    public void writeHeaders(String... headers) throws IOException {
        for (String header : headers) {
            writer.write(header + "\r\n");
        }
        writer.write("\r\n");
        writer.flush();
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
        outputStream.flush();
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }
}