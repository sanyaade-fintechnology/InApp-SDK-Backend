package com.payleven.inapp.sender;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payleven.inapp.Application;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PaylevenRequestSenderTest {

    @Value("${merchantToken}")
    private String sampleMerchantToken;

    @Value("${userToken}")
    private String sampleUserToken;

    @Autowired
    private PaylevenRequestSender requestSender;


    @Test
    public void testFullRefund() throws Exception {
        final PaymentRequest chargeRequest = new PaymentRequest();
        chargeRequest.setAmount(100);
        chargeRequest.setCurrency("EUR");
        chargeRequest.setUseCase("DEFAULT"); // mandatory
        chargeRequest.setUserToken(sampleUserToken);
        chargeRequest.setExternalId("inAPPSamplePayment-" + System.currentTimeMillis());

        final ResponseWrapper chargeResponse = requestSender.send(sampleMerchantToken, chargeRequest);

        assertThat(chargeResponse.getStatus()).isEqualTo(HttpStatus.OK);

        final ObjectMapper objectMapper = new ObjectMapper();
        final Map<String, Object> paymentResponseMap = objectMapper.readValue(chargeResponse.getContent(), new TypeReference<HashMap<String, Object>>() {
        });

        // Sleep several minutes in case a charge is not in status OK immediately.
        //TimeUnit.MINUTES.sleep(5);

        final RefundRequest refundRequest = new RefundRequest();
        refundRequest.setCurrency("EUR");
        refundRequest.setAmount(100);
        refundRequest.setExternalId("inAPPSampleRefund-" + System.currentTimeMillis());

        final String chargeExternalId = (String) paymentResponseMap.get("externalId");
        final ResponseWrapper refundResponse = requestSender.send(sampleMerchantToken, chargeExternalId, refundRequest);

        assertThat(refundResponse.getStatus()).isEqualTo(HttpStatus.OK);


        final Map<String, Object> refundResponseMap = objectMapper.readValue(refundResponse.getContent(), new TypeReference<HashMap<String, Object>>() {
        });

        assertThat(refundResponseMap)
                .containsEntry("status", "OK")
                .containsEntry("code", 0)
                .containsEntry("externalId", refundRequest.getExternalId())
                .containsKey("paymentId")
                .containsKey("transactionId");
    }

}