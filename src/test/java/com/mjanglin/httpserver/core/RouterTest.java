package com.mjanglin.httpserver.core;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.mjanglin.httpserver.core.io.RootNotFoundException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RouterTest {

    private Router router;

    @BeforeAll
    public void beforeClass() throws IOException, RootNotFoundException {
        this.router = new Router();
    }

    @Test
    public void testGetRoot() {
        assertEquals(router.getRoot(), "root");
    }

    @Test
    public void testHandler() {
        RouteHandler handler = router.getHandler("/", "GET");

        assertNotNull(handler);
    }

    @Test
    public void testHandleRequestMismatch() {
        RouteHandler handler = router.getHandler("/", "GET");
        
        assertNotEquals(handler, router.getHandler("/", "PUT"));
    }
}
