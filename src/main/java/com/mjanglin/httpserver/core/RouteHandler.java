package com.mjanglin.httpserver.core;

import java.io.BufferedReader;
import java.io.IOException;


@FunctionalInterface
public interface  RouteHandler {
    void handle(BufferedReader reader, HttpResponseWriter writer) throws IOException;
}
