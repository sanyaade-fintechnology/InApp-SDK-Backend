package com.payleven.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payleven.hmac.NotificationHmacVerifier;
import com.payleven.hmac.NotificationWrapper;
import com.payleven.hmac.PaylevenHttpHeaders;
import com.payleven.hmac.ResponseHmacBuilder;
import com.payleven.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class NotificationController {

    @Autowired
    private NotificationHmacVerifier hmacVerifier;

    @Autowired
    private PaymentService paymentService;

    @Value("${payleven.apiKey}")
    private String apiKey;

    @RequestMapping(value = "/refunds/notifications", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> receive(@RequestBody String notification, @RequestHeader HttpHeaders headers, HttpServletRequest request) throws Exception {

        final NotificationWrapper notificationWrapper = new NotificationWrapper(request.getRequestURL().toString(), notification, headers.toSingleValueMap());

        final NotificationWrapper verifiedNotification = hmacVerifier.verify(notificationWrapper);

        final boolean hmacVerified = verifiedNotification.isHmacVerified();
        if (hmacVerified) {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final RefundNotification refundNotification = objectMapper.readValue(notificationWrapper.getNotification(), RefundNotification.class);
            paymentService.update(refundNotification.getExternalId(), refundNotification.getStatus());
        }

        final String messageId = headers.toSingleValueMap().get(PaylevenHttpHeaders.X_MESSAGE_ID.toLowerCase());
        return createResponse(messageId, hmacVerified);
    }

    private ResponseEntity<BaseResponse> createResponse(String messageId, boolean hmacVerified) throws Exception {
        final BaseResponse baseResponse = new BaseResponse();
        baseResponse.setReceived(hmacVerified ? "ok" : "nok");


        final ResponseHmacBuilder responseHmacBuilder = ResponseHmacBuilder.
                newBuilder(toJson(baseResponse))
                .appendResponseStatus(200)
                .appendMessageId(messageId);


        return ResponseEntity.<BaseResponse>ok()
                .header(PaylevenHttpHeaders.X_MESSAGE_ID, messageId)
                .header(PaylevenHttpHeaders.X_BODY_HASH, responseHmacBuilder.getComputedHttpBodyHash())
                .header(PaylevenHttpHeaders.X_HMAC_TIMESTAMP, String.valueOf(responseHmacBuilder.getTimestamp()))
                .header(PaylevenHttpHeaders.X_HMAC, responseHmacBuilder.build(apiKey))
                .header(PaylevenHttpHeaders.X_HMAC_VERSION, "1")
                .body(baseResponse);
    }

    private String toJson(BaseResponse baseResponse) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(baseResponse);
    }
}
