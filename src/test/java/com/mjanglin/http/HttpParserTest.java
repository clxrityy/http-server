package com.mjanglin.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpParserTest {

    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass() {
        httpParser = new HttpParser();
    }
    
    @Test
    void parseHttpRequest() throws HttpParsingException, IOException {
        HttpRequest request = httpParser.parseHttpRequest(generateValidTestCase());
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    private InputStream generateValidTestCase() {
        String rawData = """
                         GET / HTTP/1.1\r
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

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
            StandardCharsets.US_ASCII
        ));
        
        return inputStream;
    }

    
}
