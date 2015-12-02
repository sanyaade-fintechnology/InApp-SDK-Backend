package com.payleven.controllers;


import com.jayway.restassured.response.Headers;
import com.payleven.hmac.PaylevenHttpHeaders;
import com.payleven.hmac.ResponseHmacBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponseHmacVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHmacVerifier.class);

    @Value("${payleven.apiKey}")
    private String apiKey;

    public boolean verify(int responseStatus, String responseBody, Headers responseHeaders) throws IOException {
        boolean result = false;

        String responseHmac = responseHeaders.getValue(PaylevenHttpHeaders.X_HMAC);
        String responseBodyHash = responseHeaders.getValue(PaylevenHttpHeaders.X_BODY_HASH);
        String responseTimestamp = responseHeaders.getValue(PaylevenHttpHeaders.X_HMAC_TIMESTAMP);

        if (!(StringUtils.isEmpty(responseBodyHash)
                || StringUtils.isEmpty(responseHmac)
                || StringUtils.isEmpty(responseTimestamp))) {


            ResponseHmacBuilder hmacBuilder = ResponseHmacBuilder
                    .newBuilder(responseBody, responseTimestamp)
                    .appendResponseStatus(responseStatus)
                    .appendMessageId(responseHeaders.getValue(PaylevenHttpHeaders.X_MESSAGE_ID));

            if (!responseBodyHash.equals(hmacBuilder.getComputedHttpBodyHash())) {
                LOGGER.info("Response body hash '{}' does not match computed body hash '{}'.", responseBodyHash, hmacBuilder.getComputedHttpBodyHash());
            } else {

                final String computedResponseHmac = hmacBuilder.build(apiKey);
                if (!responseHmac.equals(computedResponseHmac)) {
                    LOGGER.info("Response hmac '{}' does not match computed hmac '{}'.", responseHmac, computedResponseHmac);
                } else {
                    result = true;
                }
            }
        } else {
            LOGGER.info("Missing values for response hmac calculation in response {} {}", responseBody, responseHeaders);

        }

        return result;
    }
}
