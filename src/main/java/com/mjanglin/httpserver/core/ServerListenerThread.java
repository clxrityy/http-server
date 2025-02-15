package com.mjanglin.httpserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjanglin.httpserver.core.io.RootNotFoundException;

public class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private final int port;
    private final String webroot;
    private final ServerSocket serverSocket;
    // private final RootHandler rootHandler;
    private final ExecutorService threadPool;

    public ServerListenerThread(int prt, String wr) throws IOException, RootNotFoundException {
        this.port = prt;
        this.webroot = wr;
        this.serverSocket = new ServerSocket(this.port);
        // this.rootHandler = new RootHandler(this.webroot);
        this.threadPool = new ThreadPoolExecutor(
            5,      // Core pool size (minimum threads always running)
            50,     // Maximum pool size (upper limit for simultaneous requests)
            60,     // Keep-alive time for idle threads
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100) // Queue for pending requests if all threads are busy
        );

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

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                final Socket client = serverSocket.accept();
                LOGGER.info(" * Connection accepted: " + client.getInetAddress());
                threadPool.execute(() -> handleClient(client)); // use thread pool
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleClient(Socket clienSocket) {
        HttpConnectionWorker worker = new HttpConnectionWorker(clienSocket);
        worker.start();
    }
}