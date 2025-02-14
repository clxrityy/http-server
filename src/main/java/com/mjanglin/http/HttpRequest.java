package com.mjanglin.http;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage {
    
    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion;
    private HttpVersion httpVersion;
    private final HashMap<String, String> headers = new HashMap<>();

    HttpRequest() {

    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getHeader(String name) {
        return headers.get(name).toLowerCase();
    }

    void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod mthd : HttpMethod.values()) {
            if (mthd.name().equals(methodName)) {
                this.method = mthd;
                return;
            }
        }

        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }

    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.isEmpty()) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        } 
        this.requestTarget = requestTarget;
    }

    void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.httpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);

        if (this.httpVersion == null) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    void addHeader(String name, String value) {
        this.headers.put(name.toLowerCase(), value);
    }
}

