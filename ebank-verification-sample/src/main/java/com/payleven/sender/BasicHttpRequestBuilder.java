package com.payleven.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.payleven.hmac.PaylevenHttpHeaders;
import com.payleven.hmac.RequestHmacBuilder;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class BasicHttpRequestBuilder {

    @Value("${payleven.ebank.verification.url}")
    private String paylevenEbanVerificationUrl;

    @Value("${payleven.applicationIdentifier}")
    private String applicationIdentifier;

    @Value("${payleven.apiKey}")
    private String apiKey;

    @Value("${payleven.hmac.version}")
    private String hmacVersion;

    public RequestWrapper newRequest(String merchantToken) throws IOException {

        String paylevenUrl = paylevenEbanVerificationUrl;
        paylevenUrl = paylevenUrl.replace("%merchantToken%", merchantToken);


        final RequestHmacBuilder hmacBuilder = RequestHmacBuilder
                .newBuilder("")
                .appendMethod(HttpMethod.GET.name())
                .appendUri(paylevenUrl)
                .appendXApplicationId(applicationIdentifier);


        final Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        headers.put(PaylevenHttpHeaders.X_APPLICATION_ID, applicationIdentifier);
        headers.put(PaylevenHttpHeaders.X_HMAC, hmacBuilder.build(apiKey));
        headers.put(PaylevenHttpHeaders.X_BODY_HASH, hmacBuilder.getComputedHttpBodyHash());
        headers.put(PaylevenHttpHeaders.X_HMAC_TIMESTAMP, hmacBuilder.getTimestampAsString());
        headers.put(PaylevenHttpHeaders.X_HMAC_VERSION, hmacVersion);
        headers.put(HttpHeaders.USER_AGENT, "Payleven-InApp-SDK/1.0 iOS");

        final RequestWrapper requestWrapper = new RequestWrapper(paylevenUrl, "", headers);
        return requestWrapper;
    }

}
