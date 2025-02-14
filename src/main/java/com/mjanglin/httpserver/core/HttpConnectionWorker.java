package com.mjanglin.httpserver.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionWorker extends Thread {

    private final Socket socket;
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorker.class);

    public HttpConnectionWorker(Socket socket) {
        this.socket = socket;
    }

    private void sendResponse(OutputStream out, String status, String content) throws IOException {
        String response = "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + content.length() + "\r\n" +
                "\r\n" +
                content;
        out.write(response.getBytes());
    }

    private void sendFileResponse(OutputStream out, String status, File file, String contentType) throws IOException {
        byte[] fileData = readFileToBytes(file);
        String headers = "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + fileData.length + "\r\n" +
                "\r\n";
        out.write(headers.getBytes());
        out.write(fileData);
    }

    private byte[] readFileToBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }

    @Override
    public void run() {

        // BufferedReader reader = null;
        // PrintWriter writer = null;

        // try {
        //     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //     writer = new PrintWriter(socket.getOutputStream(), true);

        //     int _byte;
        //     while ((_byte = reader.read()) >= 0) {
        //         System.out.print((char) _byte);
        //     }

        //     while (true) {
        //         String line = reader.readLine();
        //         LOGGER.info("HEADER: " + line);
        //         if (line.equals("")) {
        //             break;
        //         }
        //         if (line.contains("GET")) {
        //             writer.println("HTTP/1.1 200 OK");
        //             writer.println("Content-Type: text/html");
        //             writer.println();
        //             writer.println("<title>Java HTTP Server</title>");
        //             writer.println();
        //             writer.println("<h1>Hello from Java HTTP Server</h1>");
        //             writer.flush();
        //             break;
        //         }
        //     }

        //     try {
        //         sleep(5000);
        //     } catch (InterruptedException e) {
        //         LOGGER.error("Problem with sleep", e);
        //     }
        //     LOGGER.info("Connection process finished.");
        // } catch (IOException e) {
        //     LOGGER.error("Problem with communication", e);
        // } finally {
        //     if (reader != null) {
        //         try {
        //             reader.close();
        //         } catch (IOException e) {
        //             LOGGER.error("Problem with closing reader", e);
        //         }
        //     }
        //     if (writer != null) {
        //         writer.close();
        //     }

        //     if (socket != null) {
        //         try {
        //             socket.close();
        //         } catch (IOException e) {
        //             LOGGER.error("Problem with closing socket", e);
        //         }
        //     }
        // }


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {
            // Read the request line (e.g., "GET / HTTP/1.1")
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            LOGGER.info("REQUEST: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2 || !requestParts[0].equalsIgnoreCase("GET")) {
                sendResponse(out, "HTTP/1.1 400 Bad Request", "Invalid request method or format.");
                return;
            }

            String requestedFile = requestParts[1]; // e.g., "/index.html"
            if (requestedFile.equals("/")) {
                requestedFile = "/index.html"; // Default to index.html
            }

            // Consume the rest of the headers
            while (!reader.readLine().isEmpty()) {
                // Just read and discard headers
            }

            File file = new File("root", requestedFile);

            if (file.exists() && !file.isDirectory()) {
                sendFileResponse(out, "200 OK", file, "text/html");
            } else {
                sendResponse(out, "404 Not Found", "<h1>404 Not Found</h1>");
            }
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } finally {
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
