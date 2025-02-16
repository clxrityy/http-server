package com.mjanglin.httpserver.core;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpResponseWriter {
    void write(String content) throws IOException;
    void write(byte[] content) throws IOException;
    void writeHeaders(String... headers) throws IOException;
    void flush() throws IOException;
    OutputStream getOutputStream();
}