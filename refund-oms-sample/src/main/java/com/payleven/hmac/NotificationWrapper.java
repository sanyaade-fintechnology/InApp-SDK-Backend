package com.payleven.hmac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

public class NotificationWrapper {

    private final String requestUrl;
    private final String notification;
    private final Map<String, String> headers = Maps.newHashMap();
    private boolean hmacVerified;
    private boolean bodyHashVerified;
    private boolean verified;

    public NotificationWrapper(String requestUrl, String notification, Map<String, String> headers) throws IOException {
        this.requestUrl = requestUrl;
        this.notification = notification;

        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    public String getNotification() {
        return notification;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setHmacVerified(boolean hmacVerified) {
        this.hmacVerified = hmacVerified;
    }

    public boolean isHmacVerified() {
        return hmacVerified;
    }

    public void setBodyHashVerified(boolean bodyHashVerified) {
        this.bodyHashVerified = bodyHashVerified;
    }

    public boolean isBodyHashVerified() {
        return bodyHashVerified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("notification", getNotification()).add("headers", headers).toString();
    }
}
