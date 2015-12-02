package com.payleven.inapp.sender;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HttpClientFactory {

    @Value("${httpclient.connection.ttl:5000}")
    private int connectionTimeToLive;

    @Value("${httpclient.request.timeout:5000}")
    private int requestTimeout;

    @Value("${httpclient.connection.request.timeout:5000}")
    private int connectionRequestTimeout;


    public CloseableHttpClient createClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(requestTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setCircularRedirectsAllowed(false)
                .setExpectContinueEnabled(false)
                .setRedirectsEnabled(true)
                .build();

        final HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionTimeToLive(connectionTimeToLive, TimeUnit.MILLISECONDS);
        builder.setDefaultRequestConfig(requestConfig);
        return builder.build();
    }
}
