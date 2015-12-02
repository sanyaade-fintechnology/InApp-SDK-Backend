package com.payleven.inapp.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.payleven.inapp.hmac.PaylevenHttpHeaders;
import com.payleven.inapp.hmac.RequestHmacBuilder;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class BasicHttpRequestBuilder {

    @Value("${payleven.onboarding.url}")
    private String paylevenOnboardingUrl;


    @Value("${payleven.applicationIdentifier}")
    private String applicationIdentifier;

    @Value("${payleven.apiKey}")
    private String apiKey;

    @Value("${payleven.hmac.version}")
    private String hmacVersion;


    public RequestWrapper newRequest(OnboardRequest onboardRequest) throws IOException {

        final ObjectMapper objectMapper = new ObjectMapper();
        final String payload = objectMapper.writeValueAsString(onboardRequest);

        String paylevenUrl = paylevenOnboardingUrl;
        paylevenUrl = paylevenUrl.replace("%countryCode%", onboardRequest.getCountryCode());
        paylevenUrl = paylevenUrl.replace("%type%", onboardRequest.getType());


        final RequestHmacBuilder hmacBuilder = RequestHmacBuilder
                .newBuilder(payload)
                .appendMethod(HttpMethod.POST.name())
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

        final RequestWrapper requestWrapper = new RequestWrapper(paylevenUrl, payload, headers);
        return requestWrapper;
    }

}
