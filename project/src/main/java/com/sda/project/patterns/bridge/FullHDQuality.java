package com.sda.project.patterns.bridge;

public class FullHDQuality implements StreamingQuality {
	@Override
	public String getQualityLabel() {
		return "Full HD";
	}

	@Override
	public int getResolutionHeight() {
		return 1080;
	}

	@Override
	public String getBitrate() {
		return "8 Mbps";
	}

	@Override
	public boolean isAvailable(String subscriptionPlan) {
		return "PREMIUM".equalsIgnoreCase(subscriptionPlan);
	}
}
