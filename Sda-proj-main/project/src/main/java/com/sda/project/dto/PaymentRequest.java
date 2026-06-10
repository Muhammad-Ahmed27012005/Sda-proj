package com.sda.project.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Double amount;
    private String currency = "USD";
    private String paymentMethod;
}