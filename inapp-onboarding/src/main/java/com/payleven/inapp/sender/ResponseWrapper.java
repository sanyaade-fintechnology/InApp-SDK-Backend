package com.payleven.inapp.sender;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseWrapper {

    private byte[] content;
    private int statusCode;
    private Map<String, String> headers = Maps.newHashMap();
    private ContentType contentType;
    private boolean hmacVerified;
    private boolean bodyHashVerified;
    private boolean verified;

    public ResponseWrapper(CloseableHttpResponse originalResponse) throws IOException {
        try {
            if (originalResponse.getEntity() != null) {
                content = ByteStreams.toByteArray(originalResponse.getEntity().getContent());
            }
            final Header[] originalHeaders = originalResponse.getAllHeaders();
            for (Header header : originalHeaders) {
                headers.put(header.getName().toUpperCase(), header.getValue());
            }
            statusCode = originalResponse.getStatusLine().getStatusCode();
            contentType = ContentType.get(originalResponse.getEntity());
        } finally {
            IOUtils.closeQuietly(originalResponse);
        }
    }


    @JsonIgnore
    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getContent() {
        return content != null ? new String(content, StandardCharsets.UTF_8) : "";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(statusCode);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("content", getContent()).add("statusCode", statusCode).add("headers", headers).toString();
    }

    public boolean isSuccessful() {
        return HttpStatus.valueOf(statusCode).is2xxSuccessful();
    }

    public boolean isClientError() {
        return HttpStatus.valueOf(statusCode).is4xxClientError();
    }

    public ContentType getContentType() {
        return contentType;
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
}
