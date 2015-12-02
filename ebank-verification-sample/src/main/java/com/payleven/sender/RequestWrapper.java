package com.payleven.sender;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestWrapper {

    private final Map<String, String> headers = Maps.newHashMap();
    private final String body;
    private final String url;

    public RequestWrapper(String url, String body, Map<String, String> headers) {
        this.url = url;
        this.body = body;
        this.headers.putAll(headers);
    }

    public HttpUriRequest asPostRequest() {
        final HttpPost httpPost = new HttpPost(url);

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            httpPost.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }

        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

        return httpPost;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("body", body).add("headers", headers).toString();
    }
}
