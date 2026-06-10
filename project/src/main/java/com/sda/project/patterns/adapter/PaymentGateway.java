package com.sda.project.patterns.adapter;

public interface PaymentGateway {
	PaymentResult processPayment(double amount, String currency, String userId);
	boolean refund(String transactionId);
}
