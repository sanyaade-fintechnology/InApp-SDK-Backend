package com.payleven.inapp.hmac;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static com.payleven.inapp.hmac.CryptoUtils.toBase64HashString;
import static com.payleven.inapp.hmac.CryptoUtils.toBase64HmacString;


public abstract class HmacBuilder {

    protected final static String NEW_LINE = "\n";
    
    private final String httpBody;
    
    private final String httpBodyHash;
    
    private final long timestamp;
    
    protected HmacBuilder(String httpBody, long timestamp) {
        this.httpBody = httpBody;
        this.httpBodyHash = computeHttpBodyHash();
        this.timestamp = timestamp;
    }
    
    public String getComputedHttpBodyHash() {
        return httpBodyHash;
    }
    
    public long getTimestamp() {
    	return timestamp;
    }
    
    public String getTimestampAsString() {
    	return String.valueOf(getTimestamp());
    }
    
    public String computeHttpBodyHash() {
        if (httpBody == null)
            throw new IllegalArgumentException("Http body has not been set");
        try {
            return toBase64HashString(httpBody);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Can't build body hash", e);
        }
    }

    public String build(String apiKey) {
        StringBuilder normalizedString = new StringBuilder();
        buildNormalizedString(normalizedString);
        try {
            return toBase64HmacString(normalizedString.toString(), apiKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Could not create HMAC for '" + normalizedString + "'.", e);
        }
    }

    protected abstract void buildNormalizedString(StringBuilder normalizedString);

}
