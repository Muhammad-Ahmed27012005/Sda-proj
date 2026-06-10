package com.sda.project.patterns.adapter;

public record PaymentResult(boolean success, String transactionId, String message) {
}
