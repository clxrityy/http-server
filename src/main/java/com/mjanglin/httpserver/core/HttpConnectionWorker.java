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

        File notFoundPage = new File("root", "404.html");
        if (notFoundPage.exists()) {
            sendFileResponse(out, "404 Not Found", notFoundPage, "text/html");
        } else {
            sendResponse(out, "404 Not Found", "<h1>404 Not Found</h1>");
        }
    }

    private void sendFileResponse(OutputStream out, String status, File file, String contentType) throws IOException {
        byte[] fileData = readFileToBytes(file);
        String headers = "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + fileData.length + "\r\n" +
                "Cache-Control: max-age=3600\r\n" +
                "\r\n";
        out.write(headers.getBytes());
        out.write(fileData);
        LOGGER.info("Sent file: " + file.getAbsolutePath() + " (" + contentType + ")");
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


    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (fileName.endsWith(".xml")) {
            return "application/xml";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".zip")) {
            return "application/zip";
        } else if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (fileName.endsWith(".woff")) {
            return "font/woff";
        } else if (fileName.endsWith(".woff2")) {
            return "font/woff2";
        } else {
            return "application/octet-stream"; // Default binary type
        }
    }    

    @Override
    public void run() {

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
                requestedFile += "/index.html"; // Default to index.html
            }

            // Normalize the requested file path
            File rootDir = new File("root");
            File file = new File(rootDir, requestedFile).getCanonicalFile();

            // Ensure the file is within the root directory
            if (!file.getPath().startsWith(rootDir.getCanonicalPath() + File.separator)) {
                sendResponse(out, "HTTP/1.1 400 Bad Request", "Invalid file path.");
                return;
            }

            // Consume the rest of the headers
            while (!reader.readLine().isEmpty()) {
                // Just read and discard headers
            }

            if (file.exists() && !file.isDirectory()) {

                // Determine Content-Type
                String contentType = getContentType(requestedFile);
                LOGGER.info("Serving file: " + requestedFile + " with content type: " + contentType);
                sendFileResponse(out, "200 OK", file, contentType);
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