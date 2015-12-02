package com.payleven.hmac;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificationHmacVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationHmacVerifier.class);

    @Value("${payleven.apiKey}")
    private String apiKey;


    public NotificationWrapper verify(NotificationWrapper request) throws IOException, NotificationVerificationException {
        // Somehow spring boot put the header keys to lowercase ...
        String notificationHmac = request.getHeader(PaylevenHttpHeaders.X_HMAC.toLowerCase());
        String notificationBodyHash = request.getHeader(PaylevenHttpHeaders.X_BODY_HASH.toLowerCase());
        String notificationTimestamp = request.getHeader(PaylevenHttpHeaders.X_HMAC_TIMESTAMP.toLowerCase());

        if (!(StringUtils.isEmpty(notificationBodyHash)
                || StringUtils.isEmpty(notificationHmac)
                || StringUtils.isEmpty(notificationTimestamp))) {


            NotificationHmacBuilder hmacBuilder = NotificationHmacBuilder
                    .newBuilder(request.getNotification(), notificationTimestamp)
                    .appendApplicationId(request.getHeader(PaylevenHttpHeaders.X_APPLICATION_ID.toLowerCase()))
                    .appendMessageId(request.getHeader(PaylevenHttpHeaders.X_MESSAGE_ID.toLowerCase()))
                    .appendRequestMethod("POST")
                    .appendRequestUrl(request.getRequestUrl());

            if (!notificationBodyHash.equals(hmacBuilder.getComputedHttpBodyHash())) {
                LOGGER.info("Notification body hash '{}' does not match computed body hash '{}'.", notificationBodyHash, hmacBuilder.getComputedHttpBodyHash());
            } else {
                request.setBodyHashVerified(true);
            }
            final String computedResponseHmac = hmacBuilder.build(apiKey);
            if (!notificationHmac.equals(computedResponseHmac)) {
                LOGGER.info("Notification hmac '{}' does not match computed hmac '{}'.", notificationHmac, computedResponseHmac);
            } else {
                request.setHmacVerified(true);
            }
        } else {
            LOGGER.info("Missing values for notification hmac calculation in request {} ", request);

        }

        return request;
    }
}
