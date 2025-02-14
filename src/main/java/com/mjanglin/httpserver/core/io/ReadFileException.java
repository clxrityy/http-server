package com.mjanglin.httpserver.core.io;

public class ReadFileException extends Exception {
    public ReadFileException(String message) {
        super(message);
    }

    public ReadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadFileException(Throwable cause) {
        super(cause);
    }

    public ReadFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
