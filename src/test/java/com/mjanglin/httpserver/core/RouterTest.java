package com.mjanglin.httpserver.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import com.mjanglin.httpserver.core.io.ReadFileException;
import com.mjanglin.httpserver.core.io.RootNotFoundException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RouterTest {
    
    @TempDir
    Path tempDir;
    
    private Router router;
    private File rootDir;
    
    @Mock
    private HttpResponseWriter mockWriter;
    
    @Mock
    private RouteHandler mockHandler;

    @BeforeEach
    public void setUp() throws IOException, RootNotFoundException {
        MockitoAnnotations.openMocks(this);
        
        // Create temporary root directory
        rootDir = new File(tempDir.toFile(), "root");
        rootDir.mkdir();
        
        // Initialize router
        router = new Router(rootDir.getPath());
    }

    @Test
    void testAddAndGetRoute() {
        router.addRoute("/test", "GET", mockHandler);
        RouteHandler handler = router.getHandler("/test", "GET");
        assertNotNull(handler);
        assertEquals(mockHandler, handler);
    }

    @Test
    void testInvalidMethod() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            router.addRoute("/test", "INVALID", mockHandler);
        });
        assertNotNull(exception);
    }

    @Test
    void testHandleStaticFileRequest() throws IOException, ReadFileException {
        // Create test static file
        File cssFile = new File(rootDir, "style.css");
        Files.write(cssFile.toPath(), "body { color: black; }".getBytes());

        // Test handling CSS file request
        router.handleRequest("/style.css", "GET", 
            new BufferedReader(new StringReader("")), mockWriter);

        // Verify response headers and content
        verify(mockWriter).writeHeaders(
            eq("HTTP/1.1 200 OK"),
            contains("text/css"),
            anyString()
        );
    }

    @Test
    void testHandleNonexistentFile() throws IOException, ReadFileException {
        router.handleRequest("/nonexistent.css", "GET", 
            new BufferedReader(new StringReader("")), mockWriter);

        verify(mockWriter).writeHeaders(
            eq("HTTP/1.1 404 Not Found"),
            eq("Content-Type: text/plain")
        );
        verify(mockWriter).write("404 Not Found");
    }

    @Test
    void testHandleDefinedRoute() throws IOException, ReadFileException {
        // Add test route
        router.addRoute("/api/test", "GET", (reader, writer) -> {
            writer.writeHeaders("HTTP/1.1 200 OK", "Content-Type: application/json");
            writer.write("{\"status\":\"success\"}");
        });

        // Test route handling
        router.handleRequest("/api/test", "GET", 
            new BufferedReader(new StringReader("")), mockWriter);

        verify(mockWriter).writeHeaders(
            eq("HTTP/1.1 200 OK"),
            eq("Content-Type: application/json")
        );
        verify(mockWriter).write("{\"status\":\"success\"}");
    }

    @Test
    void testHandle404Route() throws IOException, ReadFileException {
        router.handleRequest("/nonexistent", "GET", 
            new BufferedReader(new StringReader("")), mockWriter);

        verify(mockWriter).write(contains("404 Not Found"));
    }

    @Test
    void testPostRoute() throws IOException, ReadFileException {
        // Add test POST route
        router.addRoute("/api/post", "POST", mockHandler);

        // Test POST route exists
        RouteHandler handler = router.getHandler("/api/post", "POST");
        assertNotNull(handler);
        assertEquals(mockHandler, handler);

        // Test POST route handling
        String postData = "test=data";
        BufferedReader reader = new BufferedReader(new StringReader(postData));
        router.handleRequest("/api/post", "POST", reader, mockWriter);

        verify(mockHandler).handle(eq(reader), eq(mockWriter));
    }
}