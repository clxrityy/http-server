package com.mjanglin.httpserver.util;

public class ContentType {
    public String getContentType(String filePath) {
        if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".json")) {
            return "application/json";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".gif")) {
            return "image/gif";
        } else if (filePath.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (filePath.endsWith(".ico")) {
            return "image/x-icon";
        } else if (filePath.endsWith(".txt")) {
            return "text/plain";
        } else if (filePath.endsWith(".xml")) {
            return "application/xml";
        } else if (filePath.endsWith(".pdf")) {
            return "application/pdf";
        }
        // Add more content types as needed
        return "application/octet-stream"; // Default binary type
    }
}
