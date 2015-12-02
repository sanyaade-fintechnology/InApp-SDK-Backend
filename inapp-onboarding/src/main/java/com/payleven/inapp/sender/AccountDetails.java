package com.payleven.inapp.sender;

import java.io.Serializable;

public class AccountDetails implements Serializable {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
