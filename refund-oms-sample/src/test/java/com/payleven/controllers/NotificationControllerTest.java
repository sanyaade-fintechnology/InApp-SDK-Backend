package com.payleven.controllers;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.ResponseBody;
import com.jayway.restassured.response.ValidatableResponse;
import com.payleven.Application;
import com.payleven.hmac.HmacBuilder;
import com.payleven.hmac.PaylevenHttpHeaders;
import com.payleven.service.PaymentService;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class NotificationControllerTest {


    @Value("${local.server.port}")
    private int port;

    @Value("${payleven.applicationIdentifier}")
    private String applicationIdentifier;

    @Value("${payleven.apiKey}")
    private String apiKey;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ResponseHmacVerifier hmacVerifier;

    @Before
    public void init() {
        RestAssured.port = port;
    }

    @Test
    public void testThatNotificationIsReceivedCorrectly() {
        final String body = createSampleBody();

        given()
                .headers(validHeaders(body))
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/refunds/notifications")
                .then()
                .statusCode(HttpStatus.SC_OK);

        assertThat(paymentService.getRefundStates()).hasSize(1).containsKey("sampleRefund");
        assertThat(paymentService.getRefundStates().get("sampleRefund")).isEqualTo("SUCCESSFUL");
    }

    @Test
    public void testThatInvalidHmacReturnsNok() {
        final String body = createSampleBody();
        given()
                .headers(invalidHeaders(body))
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/refunds/notifications")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("received", equalTo("nok"));
    }

    @Test
    public void testThatResponseIsSignedCorrectly() throws Exception {
        final String body = createSampleBody();

        final ValidatableResponse validatableResponse = given()
                .headers(invalidHeaders(body))
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/refunds/notifications")
                .then()
                .statusCode(HttpStatus.SC_OK);

        final Headers responseHeaders = validatableResponse.extract().response().headers();
        final ResponseBody responseBody = validatableResponse.extract().response().body();

        assertThat(responseHeaders.getValue(PaylevenHttpHeaders.X_BODY_HASH)).isNotNull();
        assertThat(responseHeaders.getValue(PaylevenHttpHeaders.X_HMAC)).isNotNull();
        assertThat(responseHeaders.getValue(PaylevenHttpHeaders.X_MESSAGE_ID)).isEqualTo("67fbac7c-cc65-4f1d-a0ab-b187dd45cf9c");

        boolean verified = hmacVerifier.verify(HttpStatus.SC_OK, responseBody.print(), responseHeaders);
        assertThat(verified).isTrue();
    }

    private String createSampleBody() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{")
                .append("\"merchantToken\":\"sampleToken\",")
                .append("\"externalId\":\"sampleRefund\",")
                .append("\"originalExternalId\":\"sampleCharge\",")
                .append("\"originalAmount\":109,")
                .append("\"refundedAmount\":10,")
                .append("\"remainingAmount\":99,")
                .append("\"currency\":\"EUR\",")
                .append("\"status\":\"SUCCESSFUL\",")
                .append("\"created\":\"2015-10-29T10:10:13.689\",")
                .append("\"updated\":\"2015-10-29T10:12:14.676\"")
                .append("}");

        return stringBuilder.toString();
    }

    private Map<String, String> validHeaders(String body) {
        final HmacBuilder hmacBuilder = RequestHmacBuilder.newBuilder(body, 1444833854)
                .appendUri("http://localhost:" + port + "/refunds/notifications")
                .appendMethod("POST")
                .appendMessageId("67fbac7c-cc65-4f1d-a0ab-b187dd45cf9c")
                .appendXApplicationId(applicationIdentifier);

        final Map<String, String> headers = new HashMap<>();
        headers.put("X-OMS-MESSAGE-ID", "67fbac7c-cc65-4f1d-a0ab-b187dd45cf9c");
        headers.put("X-ApplicationID", applicationIdentifier);
        headers.put("X-HMAC-TIMESTAMP", "1444833854");
        headers.put("X-HMAC-VERSION", "1");
        headers.put("X-BODY-HASH", hmacBuilder.getComputedHttpBodyHash());
        headers.put("X-HMAC", hmacBuilder.build(apiKey));
        return headers;
    }

    private Map<String, String> invalidHeaders(String body) {
        final HmacBuilder hmacBuilder = RequestHmacBuilder.newBuilder(body, 1444833854).appendUri("http://localhost:" + port + "/refunds/notifications").appendMethod("POST").appendXApplicationId("com.payleven.payment.InAppSDKExample");

        final Map<String, String> headers = new HashMap<>();
        headers.put("X-OMS-MESSAGE-ID", "67fbac7c-cc65-4f1d-a0ab-b187dd45cf9c");
        headers.put("X-ApplicationID", applicationIdentifier);
        headers.put("X-HMAC-TIMESTAMP", "1444833854");
        headers.put("X-HMAC-VERSION", "1");
        headers.put("X-BODY-HASH", hmacBuilder.getComputedHttpBodyHash());
        headers.put("X-HMAC", "invalid");

        return headers;
    }


}