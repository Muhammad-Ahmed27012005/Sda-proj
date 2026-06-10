package com.sda.project.patterns.adapter;

public class StripeAdapter implements PaymentGateway {
	private final StripeGateway gateway;

	public StripeAdapter(StripeGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public PaymentResult processPayment(double amount, String currency, String userId) {
		StripeGateway.StripeChargeResult charge = gateway.charge(Math.round(amount * 100), currency);
		return new PaymentResult("succeeded".equals(charge.status()), charge.id(), "Stripe demo charge completed for user " + userId);
	}

	@Override
	public boolean refund(String transactionId) {
		return gateway.refundCharge(transactionId);
	}
}
