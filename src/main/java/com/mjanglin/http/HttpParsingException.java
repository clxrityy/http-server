package com.mjanglin.http;

public class HttpParsingException extends Exception {
    
    private final HttpStatusCode errorCode;
    
    public HttpParsingException(HttpStatusCode code) {
        super(code.MESSAGE);
        this.errorCode = code;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }
}
