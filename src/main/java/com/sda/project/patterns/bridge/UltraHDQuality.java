package com.sda.project.patterns.bridge;

public class UltraHDQuality implements StreamingQuality {
	@Override
	public String getQualityLabel() {
		return "4K Ultra HD";
	}

	@Override
	public int getResolutionHeight() {
		return 2160;
	}

	@Override
	public String getBitrate() {
		return "25 Mbps";
	}

	@Override
	public boolean isAvailable(String subscriptionPlan) {
		return "PREMIUM".equalsIgnoreCase(subscriptionPlan);
	}
}
