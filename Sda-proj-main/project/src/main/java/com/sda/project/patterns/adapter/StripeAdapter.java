package com.sda.project.patterns.adapter;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.dto.PaymentResponse;

public class StripeAdapter implements PaymentGateway {
    private final StripeApi stripeApi = new StripeApi();

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setTransactionId(stripeApi.charge(request.getAmount(), request.getCurrency()));
        response.setMessage("Stripe payment processed successfully");
        return response;
    }
}
