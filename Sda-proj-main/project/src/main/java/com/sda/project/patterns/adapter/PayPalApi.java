package com.sda.project.patterns.adapter;

import java.util.UUID;

public class PayPalApi {
    public String makePayment(Double amount, String currency) {
        return "paypal_" + currency.toLowerCase() + "_" + UUID.randomUUID();
    }
}
