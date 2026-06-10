package com.sda.project.patterns.bridge;

public class HDQuality implements StreamingQuality {
	@Override
	public String getQualityLabel() {
		return "HD";
	}

	@Override
	public int getResolutionHeight() {
		return 720;
	}

	@Override
	public String getBitrate() {
		return "3 Mbps";
	}

	@Override
	public boolean isAvailable(String subscriptionPlan) {
		return "STANDARD".equalsIgnoreCase(subscriptionPlan) || "PREMIUM".equalsIgnoreCase(subscriptionPlan);
	}
}
