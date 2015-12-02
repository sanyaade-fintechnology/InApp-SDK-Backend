package com.payleven.inapp.sender;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnboardRequest implements Serializable {

    @JsonIgnore
    private String countryCode;

    @JsonIgnore
    private String type;

    private AccountDetails accountDetails;

    private BusinessDetails businessDetails;

    private BankDetails bankDetails;

    @JsonProperty("personalDetails")
    private PersonDetails personDetails;

    private List<String> channels;

    private List<PersonDetails> proprietors;

    public OnboardRequest() {
        this(null, null);
    }

    public OnboardRequest(String countryCode, String companyType) {
        this.countryCode = countryCode;
        this.type = companyType;
    }


    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }

    public BusinessDetails getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(BusinessDetails businessDetails) {
        this.businessDetails = businessDetails;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }

    public PersonDetails getPersonDetails() {
        return personDetails;
    }

    public void setPersonDetails(PersonDetails personDetails) {
        this.personDetails = personDetails;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public List<PersonDetails> getProprietors() {
        return proprietors;
    }

    public void setProprietors(List<PersonDetails> proprietors) {
        this.proprietors = proprietors;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
