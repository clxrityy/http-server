package com.mjanglin.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10

    public HttpRequest parseHttpRequest(InputStream input) throws IOException, HttpParsingException {
        InputStreamReader reader = new InputStreamReader(input, StandardCharsets.US_ASCII);
        HttpRequest request = new HttpRequest();

        try {
            parseRequestLine(reader, request);
        } catch (IOException | HttpParsingException e) {
            LOGGER.warn("Failed to parse request line", e);
        }
        try {
            parseHeaders(reader, request);
        } catch (IOException e) {
            LOGGER.warn("Failed to parse headers", e);
        }
        
        parseBody(reader, request);

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request)
            throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int _byte;

        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    LOGGER.debug("Request line VERSION to process : {}" + processingDataBuffer.toString());

                    if (!methodParsed || !requestTargetParsed) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    try {
                        request.setHttpVersion(processingDataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    return;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            if (_byte == SP) {

                if (!methodParsed) {
                    LOGGER.debug("Request line METHOD to process : {}" + processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request line REQUEST TARGET to process : {}" + processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length());
            } else {
                processingDataBuffer.append((char)_byte);

                if (!methodParsed) {
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean crlfFound = false;

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    if (!crlfFound) {
                        crlfFound = true;

                        processSingleHeaderField(processingDataBuffer, request);
                        processingDataBuffer.delete(0, processingDataBuffer.length());
                    } else {
                        // Two CRLFs in a row means end of headers
                        return;
                    }
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

            }  else {
                crlfFound = false;
                processingDataBuffer.append((char)_byte);
            }
        }
    }

    private void processSingleHeaderField(StringBuilder processingDataBuffer, HttpRequest request) throws HttpParsingException {
        String rawHeaderField = processingDataBuffer.toString();
        Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%'*+\\-\\^_`|~0-9A-Za-z]+):\\s?(?<fieldValue>.*)\\s?$");

        Matcher matcher = pattern.matcher(rawHeaderField);

        if (matcher.matches()) {
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeader(fieldName, fieldValue);
        } else {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private void parseBody(InputStreamReader reader, HttpRequest request) {

    }

}
