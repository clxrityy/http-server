package com.mjanglin.httpserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjanglin.httpserver.core.io.ReadFileException;
import com.mjanglin.httpserver.core.io.RootHandler;
import com.mjanglin.httpserver.core.io.RootNotFoundException;

public final class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private final int port;
    private final String webroot;
    private final ServerSocket serverSocket;
    private final RootHandler rootHandler;
    private final ExecutorService threadPool;
    private final Router router;

    public ServerListenerThread(int prt, String wr) throws IOException, RootNotFoundException {
        this.port = prt;
        this.webroot = wr;
        this.serverSocket = new ServerSocket(this.port);
        this.rootHandler = new RootHandler(this.webroot);
        this.router = new Router(wr); // Pass webroot to Router
        this.threadPool = new ThreadPoolExecutor(
                5, // Core pool size (minimum threads always running)
                50, // Maximum pool size (upper limit for simultaneous requests)
                60, // Keep-alive time for idle threads
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100) // Queue for pending requests if all threads are busy
        );

        setupRoutes();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down server...");
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdown();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
            }
        }));
    }

    private void setupRoutes() throws IOException {
        Routes routes = new Routes(router);
        routes.configure();

        // Add built-in routes
        router.addRoute("/test", "GET", (reader, writer) -> {
            try {
                handleRequest("/test", "GET", reader, writer);
            } catch (ReadFileException e) {
                LOGGER.error("Error handling request: ", e);
                writer.writeHeaders("HTTP/1.1 500 Internal Server Error", "Content-Type: text/plain");
                writer.write("Internal Server Error");
                writer.flush();
            }
        });

        router.addRoute("/api/echo", "POST", (reader, writer) -> {
            try {
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

                // Send response with proper HTTP/1.1 format
                writer.writeHeaders(
                        "HTTP/1.1 200 OK",
                        "Content-Type: application/json",
                        "Content-Length: " + (bodyContent.length() + 35), // Add length of JSON wrapper
                        "Connection: close");
                writer.write("{\"status\":\"success\",\"echo\":\"" + bodyContent + "\"}");
                writer.flush();
            } catch (IOException | NumberFormatException e) {
                LOGGER.error("Error handling POST request: ", e);
                writer.writeHeaders(
                        "HTTP/1.1 500 Internal Server Error",
                        "Content-Type: text/plain",
                        "Connection: close");
                writer.write("Internal Server Error");
                writer.flush();
            }
        });
    }

    public void addRoute(String path, String method, RouteHandler handler) {
        router.addRoute(path, method, handler);
    }

    private void handleRequest(String path, String method, BufferedReader reader, HttpResponseWriter writer)
            throws IOException, ReadFileException {
        try {
            if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|html)$")) {
                router.serveStaticFile(path, writer);
                return;
            }

            RouteHandler handler = router.getHandler(path, method);
            if (handler != null) {
                handler.handle(reader, writer);
            } else {
                writer.writeHeaders(
                        "HTTP/1.1 404 Not Found",
                        "Content-Type: text/plain",
                        "Connection: close");
                writer.write("Route Not Found");
                writer.flush();
            }
        } catch (ReadFileException | IOException e) {
            LOGGER.error("Error handling request: ", e);
            writer.writeHeaders(
                    "HTTP/1.1 500 Internal Server Error",
                    "Content-Type: text/plain",
                    "Connection: close");
            writer.write("Internal Server Error");
            writer.flush();
        }
    }

    @Override
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                final Socket client = serverSocket.accept();
                LOGGER.info(" * Connection accepted: " + client.getInetAddress());
                threadPool.execute(new HttpConnectionWorker(client)); // Use thread pool
            }
        } catch (IOException e) {
            LOGGER.error("Error running server", e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing server socket", e);
            }
        }
    }
}
