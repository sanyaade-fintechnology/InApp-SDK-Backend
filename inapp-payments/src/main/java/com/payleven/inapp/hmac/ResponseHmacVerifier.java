package com.payleven.inapp.hmac;


import com.payleven.inapp.sender.ResponseWrapper;
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


    public ResponseWrapper verify(ResponseWrapper response) throws IOException, ResponseVerificationException {

        String responseHmac = response.getHeader(PaylevenHttpHeaders.X_HMAC);
        String responseBodyHash = response.getHeader(PaylevenHttpHeaders.X_BODY_HASH);
        String responseTimestamp = response.getHeader(PaylevenHttpHeaders.X_HMAC_TIMESTAMP);

        if (!(StringUtils.isEmpty(responseBodyHash)
                || StringUtils.isEmpty(responseHmac)
                || StringUtils.isEmpty(responseTimestamp))) {


            ResponseHmacBuilder hmacBuilder = ResponseHmacBuilder
                    .newBuilder(response.getContent(), responseTimestamp)
                    .appendResponseStatus(response.getStatusCode());

            if (!responseBodyHash.equals(hmacBuilder.getComputedHttpBodyHash())) {
                LOGGER.info("Response body hash '{}' does not match computed body hash '{}'.", responseBodyHash, hmacBuilder.getComputedHttpBodyHash());
            } else {
                response.setBodyHashVerified(true);
            }
            final String computedResponseHmac = hmacBuilder.build(apiKey);
            if (!responseHmac.equals(computedResponseHmac)) {
                LOGGER.info("Response hmac '{}' does not match computed hmac '{}'.", responseHmac, computedResponseHmac);
            } else {
                response.setHmacVerified(true);
            }
        } else {
            LOGGER.info("Missing values for response hmac calculation in response {} ", response);

        }

        return response;
    }
}
