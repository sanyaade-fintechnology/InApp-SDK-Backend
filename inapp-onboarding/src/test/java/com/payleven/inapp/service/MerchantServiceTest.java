package com.payleven.inapp.service;

import com.payleven.inapp.Application;
import com.payleven.inapp.domain.Merchant;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext
public class MerchantServiceTest {

    @Autowired
    private MerchantService merchantService;

    @Test
    public void testDESoleTrader() throws Exception {
        final Merchant merchant = merchantService.onboardSoleTrader("sampleDESoleTrader-" + System.currentTimeMillis() + "@payleven.de", "de");
        assertThat(merchant.getMerchantToken()).isNotEmpty();
    }

    @Test
    public void testDELimited() throws Exception {
        final Merchant merchant = merchantService.onboardLimited("sampleDELimited-" + System.currentTimeMillis() + "@payleven.de", "de");
        assertThat(merchant.getMerchantToken()).isNotEmpty();
    }

    @Test
    public void testNLSoleTrader() throws Exception {
        final Merchant merchant = merchantService.onboardSoleTrader("sampleNLSoleTrader-" + System.currentTimeMillis() + "@payleven.de", "nl");
        assertThat(merchant.getMerchantToken()).isNotEmpty();
    }

    @Test
    public void testNLLimited() throws Exception {
        final Merchant merchant = merchantService.onboardLimited("sampleNLLimited-" + System.currentTimeMillis() + "@payleven.de", "nl");
        assertThat(merchant.getMerchantToken()).isNotEmpty();
    }

    @Test
    public void testUKSoleTrader() throws Exception {
        final Merchant merchant = merchantService.onboardSoleTrader("sampleUKSoleTrader-" + System.currentTimeMillis() + "@payleven.de", "uk");
        assertThat(merchant.getMerchantToken()).isNotEmpty();
    }

    @Test
    public void testUKLimited() throws Exception {
        final Merchant merchant = merchantService.onboardLimited("sampleUKLimited-" + System.currentTimeMillis() + "@payleven.de", "uk");
        assertThat(merchant.getMerchantToken()).isNotEmpty();
    }

}
