package com.payleven.hmac;

import java.time.Instant;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class RequestHmacBuilder extends HmacBuilder {

    private String httpMethod;

    private String uri;

    private String xApplicationId;

    private RequestHmacBuilder(String requestBody) {
        this(requestBody, Instant.now().getEpochSecond());
    }

    private RequestHmacBuilder(String requestBody, long timestamp) {
        super(requestBody, timestamp);
    }

    public static RequestHmacBuilder newBuilder(String requestBody) {
        return new RequestHmacBuilder(requestBody);
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
                .append(getComputedHttpBodyHash()).append(NEW_LINE)
                .append(getTimestamp());
    }

}