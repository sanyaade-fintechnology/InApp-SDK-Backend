package com.payleven.test;

import com.payleven.hmac.ResponseVerificationException;
import com.payleven.sender.PaylevenRequestSender;
import com.payleven.sender.ResponseWrapper;
import com.payleven.service.EbankVerificationTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EbankVerificationTokenServiceTest {

    @InjectMocks
    private EbankVerificationTokenService service;

    @Mock
    private PaylevenRequestSender requestSender;

    private static final String VERIFICATION_TOKEN = "jUnitVerificationToken";

    private static final String TOKEN_RESPONSE = "{\n" +
            "  \"status\": \"OK\",\n" +
            "  \"code\": 0,\n" +
            "  \"token\": \""+ VERIFICATION_TOKEN + "\"\n" +
            "}";

    @Test
    public void getVerificationToken_WithCorrectResponse() throws Exception {

        ResponseWrapper response = mock(ResponseWrapper.class);

        when(requestSender.send(anyString())).thenReturn(response);
        when(response.isHmacVerified()).thenReturn(true);
        when(response.isSuccessful()).thenReturn(true);
        when(response.getContent()).thenReturn(TOKEN_RESPONSE);

        String verificationToken = service.getVerificationToken();

        assertEquals(VERIFICATION_TOKEN, verificationToken);
    }

    @Test(expected = RuntimeException.class)
    public void getVerificationToken_WithUnexpectedResponse() throws Exception {

        ResponseWrapper response = mock(ResponseWrapper.class);

        when(requestSender.send(anyString())).thenReturn(response);

        service.getVerificationToken();
    }
}
