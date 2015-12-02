package com.payleven.inapp.hmac;

import java.time.Instant;

public class ResponseHmacBuilder extends HmacBuilder {

    private int responseStatus;

    private ResponseHmacBuilder(String responseBody) {
        this(responseBody, Instant.now().getEpochSecond());
    }

    private ResponseHmacBuilder(String responseBody, long timestamp) {
        super(responseBody, timestamp);
    }
    
    public static ResponseHmacBuilder newBuilder(String responseBody) {
        return new ResponseHmacBuilder(responseBody);
    }
    
    public static ResponseHmacBuilder newBuilder(String responseBody, String timestamp) {
        return new ResponseHmacBuilder(responseBody, Long.valueOf(timestamp));
    }
    
    public ResponseHmacBuilder appendResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }
    
    @Override
    protected void buildNormalizedString(StringBuilder normalizedString) {
        if (responseStatus <= 0)
            throw new IllegalArgumentException("Response status has not been set");
        
        normalizedString.
                append(responseStatus).append(NEW_LINE).
                append(getComputedHttpBodyHash()).append(NEW_LINE).
                append(getTimestamp());
   }

}