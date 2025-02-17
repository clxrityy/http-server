package com.mjanglin.httpserver.config;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        // Text types
        MIME_TYPES.put(".html", "text/html");
        MIME_TYPES.put(".css", "text/css");
        MIME_TYPES.put(".txt", "text/plain");
        MIME_TYPES.put(".csv", "text/csv");
        MIME_TYPES.put(".md", "text/markdown");
        MIME_TYPES.put(".log", "text/plain");
        MIME_TYPES.put(".xml", "text/xml");

        // Application types
        MIME_TYPES.put(".js", "application/javascript");
        MIME_TYPES.put(".json", "application/json");
        MIME_TYPES.put(".xml", "application/xml");
        MIME_TYPES.put(".pdf", "application/pdf");
        MIME_TYPES.put(".zip", "application/zip");
        MIME_TYPES.put(".jsonld", "application/ld+json");
        MIME_TYPES.put(".wasm", "application/wasm");
        MIME_TYPES.put(".rtf", "application/rtf");
        MIME_TYPES.put(".doc", "application/msword");
        MIME_TYPES.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_TYPES.put(".xls", "application/vnd.ms-excel");
        MIME_TYPES.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_TYPES.put(".ppt", "application/vnd.ms-powerpoint");
        MIME_TYPES.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        MIME_TYPES.put(".exe", "application/x-msdownload");
        MIME_TYPES.put(".apk", "application/vnd.android.package-archive");
        MIME_TYPES.put(".msi", "application/x-msdownload");
        MIME_TYPES.put(".bin", "application/octet-stream");
        MIME_TYPES.put(".tar", "application/x-tar");
        MIME_TYPES.put(".gz", "application/gzip");
        MIME_TYPES.put(".7z", "application/x-7z-compressed");
        MIME_TYPES.put(".rar", "application/x-rar-compressed");
        MIME_TYPES.put(".sql", "application/sql");
        MIME_TYPES.put(".yaml", "application/x-yaml");
        MIME_TYPES.put(".yml", "application/x-yaml");
        MIME_TYPES.put(".php", "application/x-httpd-php");
        MIME_TYPES.put(".py", "text/x-python");
        MIME_TYPES.put(".java", "text/x-java-source");
        MIME_TYPES.put(".c", "text/x-csrc");
        MIME_TYPES.put(".cpp", "text/x-c++src");
        MIME_TYPES.put(".h", "text/x-chdr");
        MIME_TYPES.put(".hpp", "text/x-c++hdr");
        MIME_TYPES.put(".sh", "application/x-sh");
        MIME_TYPES.put(".bat", "application/bat");
        MIME_TYPES.put(".ps1", "application/powershell");
        MIME_TYPES.put(".dll", "application/vnd.microsoft.portable-executable");
        MIME_TYPES.put(".ipa", "application/octet-stream");
        MIME_TYPES.put(".apk", "application/vnd.android.package-archive");
        // ... add all your other application types

        // Image types
        MIME_TYPES.put(".png", "image/png");
        MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".gif", "image/gif");
        MIME_TYPES.put(".svg", "image/svg+xml");
        MIME_TYPES.put(".webp", "image/webp");
        MIME_TYPES.put(".ico", "image/x-icon");
        // ... add all your other image types

        // Font types
        MIME_TYPES.put(".woff", "font/woff");
        MIME_TYPES.put(".woff2", "font/woff2");
        MIME_TYPES.put(".ttf", "font/ttf");
        MIME_TYPES.put(".eot", "font/eot");
        MIME_TYPES.put(".otf", "font/otf");
        // ... add all your other font types

        // Audio/Video types
        MIME_TYPES.put(".mp4", "video/mp4");
        MIME_TYPES.put(".mp3", "audio/mpeg");
        MIME_TYPES.put(".wav", "audio/wav");
        MIME_TYPES.put(".avi", "video/avi");
        MIME_TYPES.put(".mov", "video/quicktime");

        // Other types
        
        // Binary types
        MIME_TYPES.put(".bin", "application/octet-stream");
        MIME_TYPES.put(".eot", "application/vnd.ms-fontobject");

    }

    public String getMimeType(String fileName) {
        String extension = getFileExtension(fileName);
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}