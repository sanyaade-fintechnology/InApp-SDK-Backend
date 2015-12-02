package com.payleven.inapp.sender;

import java.io.Serializable;

public class BusinessDetails implements Serializable {

    private String commercialRegistrationNumber;

    private String tradingCompanyName;
    private String merchantDisplayName;

    private String taxId;
    private String mainBusinessCategory;
    private String businessCategory;
    private String businessDescription;

    private OnboardingAddress companyAddress;

    public String getCommercialRegistrationNumber() {
        return commercialRegistrationNumber;
    }

    public void setCommercialRegistrationNumber(String commercialRegistrationNumber) {
        this.commercialRegistrationNumber = commercialRegistrationNumber;
    }

    public String getTradingCompanyName() {
        return tradingCompanyName;
    }

    public void setTradingCompanyName(String tradingCompanyName) {
        this.tradingCompanyName = tradingCompanyName;
    }

    public String getMerchantDisplayName() {
        return merchantDisplayName;
    }

    public void setMerchantDisplayName(String merchantDisplayName) {
        this.merchantDisplayName = merchantDisplayName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getMainBusinessCategory() {
        return mainBusinessCategory;
    }

    public void setMainBusinessCategory(String mainBusinessCategory) {
        this.mainBusinessCategory = mainBusinessCategory;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public OnboardingAddress getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(OnboardingAddress companyAddress) {
        this.companyAddress = companyAddress;
    }

}
