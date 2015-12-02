package com.payleven.inapp.controller;

import com.payleven.inapp.domain.Payment;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentView implements Serializable {

    private Long id;

    private String payer;

    private String merchant;

    private String externalId;

    private Payment.Type type;

    private Payment.Status status;

    private long amount;

    private String currencyCode;

    private Date created;

    private long refundedAmount;

    private Long reference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Payment.Type getType() {
        return type;
    }

    public void setType(Payment.Type type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    public Payment.Status getStatus() {
        return status;
    }

    public void setStatus(Payment.Status status) {
        this.status = status;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayer() {
        return payer;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getMerchant() {
        return merchant;
    }

    public Long getReference() {
        return reference;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    public long getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(long refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public boolean isFullRefunded() {
        return refundedAmount == amount;
    }
}
