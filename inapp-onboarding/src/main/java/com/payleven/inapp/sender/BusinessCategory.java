package com.payleven.inapp.sender;

public enum BusinessCategory {
    TAXI("4121", "Taxicabs &amp; Limousines"), DEFAULT("0", "Unknown");

    private final String mcc;
    private final String description;

    BusinessCategory(String mcc, String description) {
        this.mcc = mcc;
        this.description = description;
    }

    public String getMcc() {
        return mcc;
    }

    public String getDescription() {
        return description;
    }
}
