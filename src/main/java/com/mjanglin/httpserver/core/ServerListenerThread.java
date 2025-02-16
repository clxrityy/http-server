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
        // if (wr == null) {
            
        // }

        this.port = prt;
        this.webroot = wr;
        this.serverSocket = new ServerSocket(this.port);
        this.rootHandler = new RootHandler(this.webroot);
        this.router = new Router(); // Pass webroot to Router
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

    private void setupRoutes() {
        router.addRoute("/", "GET", (reader, writer) -> {
            try {
                handleRequest("/", "GET", reader, writer);
            } catch (ReadFileException e) {
                LOGGER.error("Error reading file for root path: ", e);
            }
        });

        router.addRoute("/test", "GET", (reader, writer) -> {
            writer.writeHeaders("HTTP/1.1 200 OK", "Content-Type: text/plain");
            writer.write("Hello from /test route");
            writer.flush();
        });
    }

    public void addRoute(String path, String method, RouteHandler handler) {
        router.addRoute(path, method, handler);
    }

    private void handleRequest(String path, String method, BufferedReader reader, HttpResponseWriter writer)
            throws IOException, ReadFileException {
        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|html)$")) {
            router.serveStaticFile(path, writer);
            return;
        }

        RouteHandler handler = router.getHandler(path, method);
        if (handler != null) {
            handler.handle(reader, writer);
        } else {
            writer.writeHeaders("HTTP/1.1 404 Not Found", "Content-Type: text/plain");
            writer.write("Route Not Found");
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
