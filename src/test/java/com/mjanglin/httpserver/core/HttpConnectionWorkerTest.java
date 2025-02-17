package com.mjanglin.httpserver.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpConnectionWorkerTest {

    @TempDir
    Path tempDir;

    @Mock
    private Socket socket;

    private File rootDir;
    private HttpConnectionWorker httpConnectionWorker;
    private ByteArrayOutputStream outputStream;


    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);

        // create a temporary root directory
        rootDir = new File(tempDir.toFile(), "root");
        rootDir.mkdir();

        // set up mock socket
        outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        httpConnectionWorker = new HttpConnectionWorker(socket);
    }

    @Test
    void testGetContentType() throws Exception {
        assertEquals("text/html", invokeGetContentType("test.html"));
        assertEquals("application/json", invokeGetContentType("test.json"));
        assertEquals("text/css", invokeGetContentType("test.css"));
        assertEquals("application/javascript", invokeGetContentType("test.js"));
        assertEquals("image/png", invokeGetContentType("test.png"));
        assertEquals("image/jpeg", invokeGetContentType("test.jpg"));
        assertEquals("image/gif", invokeGetContentType("test.gif"));
        assertEquals("text/plain", invokeGetContentType("test.txt"));
        assertEquals("application/octet-stream", invokeGetContentType("test.unknown"));
    }

    @Test
    @SuppressWarnings("CallToThreadRun")
    void testHandleValidGetRequest() throws IOException {
        // Create test file
        File testFile = new File(rootDir, "index.html");
        Files.write(testFile.toPath(), "<html><body>Hello, world!</body></html>".getBytes());

        // Set up input stream with GET request
        String getRequest = "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        when(socket.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(getRequest.getBytes()));

        // Run the worker
        httpConnectionWorker.run();

        // Verify response
        String response = outputStream.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
    }

    @Test
    @SuppressWarnings("CallToThreadRun")
    void test404Response() throws IOException {
        // Set up input stream with GET request for non-existent file
        String getRequest = "GET /nonexistent.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        when(socket.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(getRequest.getBytes()));

        // Run the worker
        httpConnectionWorker.run();

        // Verify response
        String response = outputStream.toString();
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("<h1>404 Not Found</h1>"));
    }

    @Test
    @SuppressWarnings("CallToThreadRun")
    void testInvalidRequest() throws IOException {
        // Set up input stream with invalid request
        String request = "INVALID /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
        when(socket.getInputStream()).thenReturn(inputStream);

        // Run the worker
        httpConnectionWorker.run();

        // Verify response
        String response = outputStream.toString();
        assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
    }

    @Test
    void testReadFileToBytes() throws Exception {
        // Create test file with known content
        String content = "Test content";
        File testFile = new File(rootDir, "test.txt");
        Files.write(testFile.toPath(), content.getBytes());

        // Read file using private method
        byte[] fileBytes = invokeReadFileToBytes(testFile);

        // Verify content
        assertArrayEquals(content.getBytes(), fileBytes);
    }

    @Test
    void testDirectoryTraversal() throws IOException {
        // Create test file
        File testFile = new File(rootDir, "index.html");
        Files.write(testFile.toPath(), "<html><body>Hello, world!</body></html>".getBytes());

        // Set up input stream with directory traversal request
        String getRequest = "GET /../index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        when(socket.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(getRequest.getBytes()));

        // Run the worker
        httpConnectionWorker.run();

        // Verify response
        String response = outputStream.toString();
        assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
    }

    // Helper method to invoke private getContentType method
    private String invokeGetContentType(String fileName) throws Exception {
        Method method = HttpConnectionWorker.class.getDeclaredMethod("getContentType", String.class);
        method.setAccessible(true);
        return (String) method.invoke(httpConnectionWorker, fileName);
    }

    // Helper method to invoke private readFileToBytes method
    private byte[] invokeReadFileToBytes(File file) throws Exception {
        Method method = HttpConnectionWorker.class.getDeclaredMethod("readFileToBytes", File.class);
        method.setAccessible(true);
        return (byte[]) method.invoke(httpConnectionWorker, file);
    }

}
