package com.payleven.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payleven.hmac.ResponseVerificationException;
import com.payleven.sender.PaylevenRequestSender;
import com.payleven.sender.ResponseWrapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class EbankVerificationTokenService {

    private static final Logger LOGGER = LogManager.getLogger(EbankVerificationTokenService.class);

    @Value("${merchantToken}")
    private String merchantToken;

    @Autowired
    private PaylevenRequestSender requestSender;

    public String getVerificationToken() throws Exception{

        ResponseWrapper response = requestSender.send(merchantToken);

        LOGGER.debug(response.getContent());
        if (response.isSuccessful() && response.isHmacVerified()) {
            final ObjectMapper objectMapper = new ObjectMapper();
            final Map<String, String> map = objectMapper.readValue(response.getContent(), new TypeReference<HashMap<String, String>>() {});
            return map.get("token");
        }

        throw new RuntimeException("Unexpected answer from the server");
    }
}
