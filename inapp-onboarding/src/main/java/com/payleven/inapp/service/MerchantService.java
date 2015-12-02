package com.payleven.inapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payleven.inapp.domain.Merchant;
import com.payleven.inapp.domain.MerchantRepository;
import com.payleven.inapp.sender.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class MerchantService {

    private static final Logger LOGGER = LogManager.getLogger(MerchantService.class);

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PaylevenRequestSender sender;

    @Transactional
    public Merchant onboardSoleTrader(String email, String countryCode) throws Exception {

        final OnboardRequest request = new OnboardRequest(countryCode, "soletrader");
        request.setAccountDetails(sampleAccountDetails(email));
        request.setBankDetails(sampleBankDetails(countryCode));
        request.setBusinessDetails(sampleBusinessDetails());
        request.setPersonDetails(samplePersonDetails("sample", "user", 100));
        request.setChannels(Arrays.asList("ecom"));
        request.setProprietors(Arrays.asList(samplePersonDetails("sample", "user", 100)));

        return onboard(request);
    }

    @Transactional
    public Merchant onboardLimited(String email, String countryCode) throws Exception {

        final OnboardRequest request = new OnboardRequest(countryCode, "limited");
        request.setAccountDetails(sampleAccountDetails(email));
        request.setBankDetails(sampleBankDetails(getSampleIban(countryCode)));
        request.setBusinessDetails(sampleBusinessDetails());
        request.setPersonDetails(samplePersonDetails("sample", "user", 50));
        request.setChannels(Arrays.asList("ecom"));
        request.setProprietors(Arrays.asList(samplePersonDetails("sample", "user", 50), samplePersonDetails("sample", "user2", 50)));

        return onboard(request);

    }

    private Merchant onboard(OnboardRequest request) throws Exception {
        final Merchant merchant = new Merchant();
        merchant.setEmail(request.getAccountDetails().getEmail());
        merchant.setCountryCode(request.getCountryCode());
        merchant.setType(request.getType());
        merchantRepository.save(merchant);
        final ResponseWrapper response = sender.send(request);
        LOGGER.info("payleven response = " + response);
        if (response.isSuccessful() && response.isHmacVerified()) {
            final ObjectMapper objectMapper = new ObjectMapper();
            final Map<String, String> map = objectMapper.readValue(response.getContent(), new TypeReference<HashMap<String, String>>() {});
            merchant.setMerchantToken(map.get("merchantToken"));
            merchantRepository.save(merchant);
        }
        return merchant;

    }


    private PersonDetails samplePersonDetails(String firstname, String lastname, int shareHolding) {
        final PersonDetails result = new PersonDetails();
        result.setDobDay(5);
        result.setDobMonth(8);
        result.setDobYear(1980);
        result.setFirstName(firstname);
        result.setLastName(lastname);
        result.setPrivateAddress(privateAddress());
        // percentage of shares
        result.setShareHolding(shareHolding);
        return result;
    }

    private OnboardingAddress privateAddress() {
        final OnboardingAddress result = new OnboardingAddress();
        result.setStreet("SampleStreet");
        result.setHouseNumber("1");
        result.setCity("SampleTown");
        result.setCellPhone("01234567890");
        result.setZipCode("unknown");
        result.setLandLine("210123456789");
        result.setCounty("sampleCounty");
        return result;
    }

    private BusinessDetails sampleBusinessDetails() {
        final BusinessDetails result = new BusinessDetails();
        result.setBusinessDescription("sample business");
        result.setCommercialRegistrationNumber("sampleRegistrationNumber");
        result.setCompanyAddress(companyAddress());
        result.setMainBusinessCategory(BusinessCategory.TAXI.getDescription());
        result.setBusinessCategory(BusinessCategory.TAXI.getMcc());
        result.setMerchantDisplayName("sample merchant " + System.currentTimeMillis());
        result.setTaxId("sampleTaxId");
        result.setTradingCompanyName("sampleTradingCompany");

        return result;
    }

    private OnboardingAddress companyAddress() {
        final OnboardingAddress result = new OnboardingAddress();
        result.setStreet("SampleStreet");
        result.setHouseNumber("1");
        result.setCity("SampleTown");
        result.setCellPhone("01234567890");
        result.setZipCode("unknown");
        result.setLandLine("210123456789");
        result.setCounty("sampleCounty");
        return result;
    }

    private BankDetails sampleBankDetails(String iban) {
        final BankDetails result = new BankDetails();
        result.setAccountHolder("sample account holder");
        result.setIban(iban);
        result.setBic("OKOYFIHH");
        result.setBankName("sampleBank");
        return result;
    }

    private AccountDetails sampleAccountDetails(String email) {
        final AccountDetails result = new AccountDetails();
        result.setEmail(email);
        return result;
    }


    public Page<Merchant> getMerchants(int page, int pageSize) {
        final PageRequest pageRequest = new PageRequest(page, pageSize);
        return merchantRepository.findAll(pageRequest);
    }

    private String getSampleIban(String countryCode) {
        final Map<String, String> sampleIbans = new HashMap<>();
        sampleIbans.put("de", "DE12500105170648489890");
        sampleIbans.put("nl", "NL18ABNA0484869868");
        sampleIbans.put("uk", "GB32ESSE40486562136016");

        return sampleIbans.get(countryCode);
    }
}
