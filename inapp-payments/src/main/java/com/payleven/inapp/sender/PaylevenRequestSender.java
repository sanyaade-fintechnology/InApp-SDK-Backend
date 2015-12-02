package com.payleven.inapp.sender;

import com.payleven.inapp.hmac.ResponseHmacVerifier;
import com.payleven.inapp.hmac.ResponseVerificationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PaylevenRequestSender {

    @Autowired
    private BasicHttpRequestBuilder requestBuilder;

    @Autowired
    private HttpClientFactory httpClientFactory;

    @Autowired
    private ResponseHmacVerifier responseHmacVerifier;

    public ResponseWrapper send(String merchantToken, PaymentRequest paymentRequest) throws IOException, ResponseVerificationException {

        final RequestWrapper requestWrapper = requestBuilder.newRequest(merchantToken, paymentRequest);

        final CloseableHttpClient client = httpClientFactory.createClient();

        final CloseableHttpResponse response = client.execute(requestWrapper.asPostRequest());
        final ResponseWrapper responseWrapper = new ResponseWrapper(response);

        return responseHmacVerifier.verify(responseWrapper);
    }

    public ResponseWrapper send(String merchantToken, String originalExternalId, RefundRequest refundRequest) throws IOException, ResponseVerificationException {

        final RequestWrapper requestWrapper = requestBuilder.newRequest(merchantToken, originalExternalId, refundRequest);

        final CloseableHttpClient client = httpClientFactory.createClient();

        final CloseableHttpResponse response = client.execute(requestWrapper.asPostRequest());
        final ResponseWrapper responseWrapper = new ResponseWrapper(response);

        return responseHmacVerifier.verify(responseWrapper);
    }
}
