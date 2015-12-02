package com.payleven.inapp.sender;

import java.io.Serializable;

public class PersonDetails implements Serializable {
    //@NotEmpty
    private String firstName;

    //@NotEmpty
    private String lastName;

    //@NotNull
    private Integer dobDay;

    //@NotNull
    private Integer dobMonth;

    //@NotNull
    private Integer dobYear;

    //@NotNull(groups = DELimitedGroup.class)
    private Integer shareHolding;

    //@NotNull
    //@Valid
    private OnboardingAddress privateAddress;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getDobDay() {
        return dobDay;
    }

    public void setDobDay(Integer dobDay) {
        this.dobDay = dobDay;
    }

    public Integer getDobMonth() {
        return dobMonth;
    }

    public void setDobMonth(Integer dobMonth) {
        this.dobMonth = dobMonth;
    }

    public Integer getDobYear() {
        return dobYear;
    }

    public void setDobYear(Integer dobYear) {
        this.dobYear = dobYear;
    }

    public OnboardingAddress getPrivateAddress() {
        return privateAddress;
    }

    public void setPrivateAddress(OnboardingAddress privateAddress) {
        this.privateAddress = privateAddress;
    }

    public Integer getShareHolding() {
        return shareHolding;
    }

    public void setShareHolding(Integer shareHolding) {
        this.shareHolding = shareHolding;
    }

}
