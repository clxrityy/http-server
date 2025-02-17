package com.mjanglin.httpserver.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjanglin.httpserver.core.io.ReadFileException;
import com.mjanglin.httpserver.core.io.RootHandler;
import com.mjanglin.httpserver.core.io.RootNotFoundException;

public class Router {
    private final Map<String, RouteHandler> getRoutes = new HashMap<>();
    private final Map<String, RouteHandler> postRoutes = new HashMap<>();
    private final String staticFileDirectory; // Change this to your actual static files directory
    private final RootHandler rootHandler;
    private final static Logger LOGGER = LoggerFactory.getLogger(Router.class);

    public Router(String rootPath) throws IOException, RootNotFoundException {
        this.staticFileDirectory = rootPath;
        this.rootHandler = new RootHandler(this.staticFileDirectory);
    }

    public String getRoot() {
        return this.staticFileDirectory;
    }

    public void addRoute(String path, String method, RouteHandler handler) {
        if ("GET".equalsIgnoreCase(method)) {
            getRoutes.put(path, handler);
        } else if ("POST".equalsIgnoreCase(method)) {
            postRoutes.put(path, handler);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    public RouteHandler getHandler(String path, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return getRoutes.get(path);
        } else if ("POST".equalsIgnoreCase(method)) {
            return postRoutes.get(path);
        }
        return null;
    }

    public void handleRequest(String path, String method, BufferedReader reader, HttpResponseWriter writer)
            throws IOException, ReadFileException {
        // Check if it's a static file request (CSS, JS, images, etc.)
        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|json|txt|svg|wav|mp3|webmanifest)$")) {
            serveStaticFile(path, writer);
            return;
        }

        // Otherwise, check if there's a route handler
        RouteHandler handler = getHandler(path, method);
        if (handler != null) {
            handler.handle(reader, writer);
        } else {
            writer.write("HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\nRoute Not Found");
            writer.flush();
        }
    }

    public void serveStaticFile(String path, HttpResponseWriter writer) throws IOException, ReadFileException {
        // Remove leading slash if present and combine with root directory
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        File file = new File(staticFileDirectory, cleanPath);

        // DEBUG LOGGING
        LOGGER.debug("Attempting to serve static file: " + file.getAbsolutePath());
        LOGGER.debug("File exists: " + file.exists());
        LOGGER.debug("Is file: " + file.isFile());

        if (!file.exists() || !file.isFile()) {
            LOGGER.info("File not found: " + file.getAbsolutePath());
            writer.writeHeaders("HTTP/1.1 404 Not Found", "Content-Type: text/plain");
            writer.write("404 Not Found");
            return;
        }

        String contentType = rootHandler.getFileMimeType(cleanPath);
        byte[] fileBytes = rootHandler.getFileByteArrayData(cleanPath);

        // DEBUG LOGGING
        LOGGER.debug("Content-Type determined: " + contentType);
        LOGGER.debug("File size: " + fileBytes.length);

        contentType = rootHandler.getFileMimeType(file.getAbsolutePath());

        writer.writeHeaders(
                "HTTP/1.1 200 OK",
                "Content-Type: " + contentType,
                "Content-Length: " + fileBytes.length);
        writer.write(new String(fileBytes));
        writer.flush();
    }
}
