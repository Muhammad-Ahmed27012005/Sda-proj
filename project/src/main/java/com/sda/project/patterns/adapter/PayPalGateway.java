package com.sda.project.patterns.adapter;

import java.util.UUID;

public class PayPalGateway {
	public String sendPayment(double amount, String currency) {
		return "PAYPAL-TXN-" + UUID.randomUUID();
	}

	public boolean sendRefund(String transactionId) {
		return transactionId != null && transactionId.startsWith("PAYPAL-TXN-");
	}
}
