package com.sda.project.patterns.adapter;

import java.util.UUID;

public class StripeGateway {
	public StripeChargeResult charge(long amountInCents, String currency) {
		return new StripeChargeResult("STRIPE-TXN-" + UUID.randomUUID(), "succeeded", amountInCents, currency);
	}

	public boolean refundCharge(String chargeId) {
		return chargeId != null && chargeId.startsWith("STRIPE-TXN-");
	}

	public record StripeChargeResult(String id, String status, long amountInCents, String currency) {
	}
}
