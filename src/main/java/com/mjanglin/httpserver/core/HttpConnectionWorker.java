package com.mjanglin.httpserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionWorker extends Thread {

    private final Socket socket;
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorker.class);

    public HttpConnectionWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            int _byte;
            while ((_byte = reader.read()) >= 0) {
                System.out.print((char) _byte);
            }

            while (true) {
                String line = reader.readLine();
                LOGGER.info("HEADER: " + line);
                if (line.equals("")) {
                    break;
                }
                if (line.contains("GET")) {
                    writer.println("HTTP/1.1 200 OK");
                    writer.println("Content-Type: text/html");
                    writer.println();
                    writer.println("<title>Java HTTP Server</title>");
                    writer.println();
                    writer.println("<h1>Hello from Java HTTP Server</h1>");
                    writer.flush();
                    break;
                }
            }

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                LOGGER.error("Problem with sleep", e);
            }
            LOGGER.info("Connection process finished.");
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Problem with closing reader", e);
                }
            }
            if (writer != null) {
                writer.close();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("Problem with closing socket", e);
                }
            }
        }
    }
}
