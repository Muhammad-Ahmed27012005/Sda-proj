package com.sda.project.service;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.dto.PaymentResponse;
import com.sda.project.patterns.adapter.PayPalAdapter;
import com.sda.project.patterns.adapter.PaymentGateway;
import com.sda.project.patterns.adapter.StripeAdapter;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentGateway gateway = "PAYPAL".equalsIgnoreCase(request.getPaymentMethod())
                ? new PayPalAdapter()
                : new StripeAdapter();
        return gateway.processPayment(request);
    }
}
