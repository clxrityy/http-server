package com.mjanglin.httpserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjanglin.httpserver.core.io.RootNotFoundException;

public class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private final int port;
    private final String webroot;
    private final ServerSocket serverSocket;
    // private final RootHandler rootHandler;

    public ServerListenerThread(int prt, String wr) throws IOException, RootNotFoundException {
        this.port = prt;
        this.webroot = wr;
        this.serverSocket = new ServerSocket(this.port);
        // this.rootHandler = new RootHandler(this.webroot);
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                final Socket client = serverSocket.accept();

                LOGGER.info(" * Connection accepted: " + client.getInetAddress());

                HttpConnectionWorker worker = new HttpConnectionWorker(client);
                worker.start();
                
            }
            // serverSocket.close();

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
}