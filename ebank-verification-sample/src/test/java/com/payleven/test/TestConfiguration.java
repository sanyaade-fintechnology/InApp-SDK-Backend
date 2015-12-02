package com.payleven.test;

import com.payleven.service.EbankVerificationTokenService;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.when;

@Configuration
public class TestConfiguration {

    public TestConfiguration(){}

    @Bean
    public EbankVerificationTokenService ebankVerificationTokenService() throws Exception {
        EbankVerificationTokenService mock = Mockito.mock(EbankVerificationTokenService.class);

        when(mock.getVerificationToken()).thenReturn("jUnitMockToken");
        return mock;
    }
}
