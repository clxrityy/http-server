package com.mjanglin.http;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeadersParseTest {

    private HttpParser httpParser;
    private Method parseHeadersMethod;

    @BeforeAll
    public void beforeClass() throws NoSuchMethodException {
        httpParser = new HttpParser();
        Class<HttpParser> cls = HttpParser.class;
        this.parseHeadersMethod = cls.getDeclaredMethod("parseHeaders", InputStreamReader.class, HttpRequest.class);
        this.parseHeadersMethod.setAccessible(true);
    }

    @Test
    public void testPrivateMethod() throws InvocationTargetException, IllegalAccessException {
        this.parseHeadersMethod.invoke(httpParser, new InputStreamReader(new ByteArrayInputStream("".getBytes())), new HttpRequest());
    }
}
