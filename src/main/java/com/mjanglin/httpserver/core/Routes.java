package com.mjanglin.httpserver.core;

import java.io.IOException;

public class Routes {
    private final Router router;

    public Routes(Router router) {
        this.router = router;
    }

    public void configure() throws IOException {
        // define default routes
        // default router (/) already handled

        router.addRoute("/api/hello", "GET", (reader, writer) -> {
            writer.writeHeaders("HTTP/1.1 200 OK", "Content-Type: text/html");
            writer.write("<h1>Welcome to the first API endpoint!");
            writer.flush();
        });

        router.addRoute("/api/echo", "POST", (reader, writer) -> {
            // Read the headers first
            String line;
            int contentLength = 0;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring(15).trim());
                }
            }
    
            // Read the body
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            String bodyContent = new String(body);
    
            // Send response
            writer.writeHeaders(
                "HTTP/1.1 200 OK",
                "Content-Type: application/json",
                "Connection: close"
            );
            writer.write("{\"status\":\"success\",\"echo\":\"" + bodyContent + "\"}");
            writer.flush();
        });
    }
}
