package com.payleven.inapp.controller;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

public class CreateMerchantApiRequest implements Serializable {

    @NotBlank
    private String countryCode;

    @NotBlank
    private String type;

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
