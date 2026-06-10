package com.sda.project.patterns.bridge;

public class SDQuality implements StreamingQuality {
	@Override
	public String getQualityLabel() {
		return "SD";
	}

	@Override
	public int getResolutionHeight() {
		return 480;
	}

	@Override
	public String getBitrate() {
		return "1 Mbps";
	}

	@Override
	public boolean isAvailable(String subscriptionPlan) {
		return true;
	}
}
