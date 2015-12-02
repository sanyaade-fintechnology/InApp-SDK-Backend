package com.payleven.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private Map<String, String> refundStates = new HashMap<String, String>();

    public void update(String refundId, String status) {
        refundStates.put(refundId, status);
    }

    public Map<String, String> getRefundStates() {
        return refundStates;
    }
}
