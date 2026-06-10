package com.sda.project.patterns.adapter;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.dto.PaymentResponse;

public class PayPalAdapter implements PaymentGateway {
    private final PayPalApi payPalApi = new PayPalApi();

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setTransactionId(payPalApi.makePayment(request.getAmount(), request.getCurrency()));
        response.setMessage("PayPal payment processed successfully");
        return response;
    }
}
