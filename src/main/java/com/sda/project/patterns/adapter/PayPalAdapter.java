package com.sda.project.patterns.adapter;

public class PayPalAdapter implements PaymentGateway {
	private final PayPalGateway gateway;

	public PayPalAdapter(PayPalGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public PaymentResult processPayment(double amount, String currency, String userId) {
		String transactionId = gateway.sendPayment(amount, currency);
		return new PaymentResult(true, transactionId, "PayPal demo payment completed for user " + userId);
	}

	@Override
	public boolean refund(String transactionId) {
		return gateway.sendRefund(transactionId);
	}
}
