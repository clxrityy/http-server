package com.mjanglin.httpserver.config;

public class HttpConfigExeception extends RuntimeException {
    public HttpConfigExeception() {

    }

    public HttpConfigExeception(String message) {
        super(message);
    }

    public HttpConfigExeception(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpConfigExeception(Throwable cause) {
        super(cause);
    }
}
