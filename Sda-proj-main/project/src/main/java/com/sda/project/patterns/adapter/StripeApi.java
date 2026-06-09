package com.sda.project.patterns.adapter;

import java.util.UUID;

public class StripeApi {
    public String charge(Double amount, String currency) {
        return "stripe_" + currency.toLowerCase() + "_" + UUID.randomUUID();
    }
}
