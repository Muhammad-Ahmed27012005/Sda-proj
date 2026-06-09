package com.sda.project.patterns.adapter;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.dto.PaymentResponse;

public interface PaymentGateway {
    PaymentResponse processPayment(PaymentRequest request);
}
