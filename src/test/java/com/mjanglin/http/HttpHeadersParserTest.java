package com.mjanglin.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeadersParserTest {

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
    public void testSimpleSingleHeader() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeadersMethod.invoke(httpParser, generateSimepleSingerHeaderMessage(), request);

        assertEquals(1, request.getHeaderNames().size());
        assertEquals("localhost:5006", request.getHeader("host"));
    }

    @Test
    public void testMulitpleHeaders() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeadersMethod.invoke(httpParser, generateMulitpleHeaderMessage(), request);

        assertEquals(15, request.getHeaderNames().size());
        assertEquals("localhost:5006", request.getHeader("host"));

    }

    @Test
    public void testSpaceBeforeColonError() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        try {
            parseHeadersMethod.invoke(httpParser, generateSpaceBeforeColonError(), request);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof HttpParsingException exception) {
                assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, exception.getErrorCode());
            }
        }
    }

    private InputStreamReader generateSimepleSingerHeaderMessage() {
        String rawData = "Host: localhost:5006\r\n";
        InputStream inputStreamm = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));

        InputStreamReader reader = new InputStreamReader(inputStreamm, StandardCharsets.US_ASCII);

        return reader;
    }

    private InputStreamReader generateMulitpleHeaderMessage() {
        String rawData = """
                         Host: localhost:5006\r
                         Connection: keep-alive\r
                         Cache-Control: max-age=0\r
                         sec-ch-ua: "Chromium";v="130", "Google Chrome";v="130", "Not?A_Brand";v="99"\r
                         sec-ch-ua-mobile: ?0\r
                         sec-ch-ua-platform: "macOS"\r
                         Upgrade-Insecure-Requests: 1\r
                         User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36\r
                         Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r
                         Sec-Fetch-Site: none\r
                         Sec-Fetch-Mode: navigate\r
                         Sec-Fetch-User: ?1\r
                         Sec-Fetch-Dest: document\r
                         Accept-Encoding: gzip, deflate, br, zstd\r
                         Accept-Language: en-US,en;q=0.9\r
                         \r
                         """ //
                          ;
        InputStream inputStreamm = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));

        InputStreamReader reader = new InputStreamReader(inputStreamm, StandardCharsets.US_ASCII);

        return reader;
    }

    private InputStreamReader generateSpaceBeforeColonError() {
        String rawData = "Host : localhost:5006\r\n";
        InputStream inputStreamm = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));

        InputStreamReader reader = new InputStreamReader(inputStreamm, StandardCharsets.US_ASCII);

        return reader;
    }


    @Test
    public void testPrivateMethod() throws InvocationTargetException, IllegalAccessException {
        this.parseHeadersMethod.invoke(httpParser, new InputStreamReader(new ByteArrayInputStream("".getBytes())), new HttpRequest());
    }
}
