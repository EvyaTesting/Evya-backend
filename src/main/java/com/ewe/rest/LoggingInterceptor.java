package com.ewe.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingInterceptor implements ClientHttpRequestInterceptor { //(1)

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        if (log.isInfoEnabled()) {
           // String requestBody = new String(body, StandardCharsets.UTF_8);

            log.info("Request Header {}", request.getHeaders()); //(2)
            //log.debug("Request Body {}", requestBody);
        }

        ClientHttpResponse response = execution.execute(request, body); //(3)

        if (log.isInfoEnabled()) {
            log.info("Response Header {}", response.getHeaders()); // (4)
            log.info("Response Status Code {}", response.getStatusCode()); // (5)
        }

        return response; // (6)
    }

}