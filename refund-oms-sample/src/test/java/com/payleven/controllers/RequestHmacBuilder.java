package com.payleven.controllers;

import com.payleven.hmac.HmacBuilder;

import java.time.Instant;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class RequestHmacBuilder extends HmacBuilder {

    private String httpMethod;

    private String uri;

    private String xApplicationId;
    private String messageId;

    private RequestHmacBuilder(String requestBody, long timestamp) {
        super(requestBody, timestamp);
    }

    public static RequestHmacBuilder newBuilder(String requestBody, long timestamp) {
        return new RequestHmacBuilder(requestBody, timestamp);
    }

    public RequestHmacBuilder appendMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public RequestHmacBuilder appendUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RequestHmacBuilder appendXApplicationId(String xApplicationId) {
        this.xApplicationId = xApplicationId;
        return this;
    }

    @Override
    protected void buildNormalizedString(StringBuilder normalizedString) {
        if (isEmpty(uri))
            throw new IllegalArgumentException("Uri has not been set");
        else if (isEmpty(httpMethod))
            throw new IllegalArgumentException("HttpMethod has not been set");
        else if (isEmpty(xApplicationId))
            throw new IllegalArgumentException("XApplicationId has not been set");

        normalizedString
                .append(httpMethod).append(NEW_LINE)
                .append(uri).append(NEW_LINE)
                .append(xApplicationId).append(NEW_LINE)
                .append(messageId).append(NEW_LINE)
                .append(getComputedHttpBodyHash()).append(NEW_LINE)
                .append(getTimestamp());
    }

    public RequestHmacBuilder appendMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }
}