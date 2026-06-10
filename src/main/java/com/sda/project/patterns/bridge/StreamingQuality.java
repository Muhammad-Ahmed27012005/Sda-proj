package com.sda.project.patterns.bridge;

public interface StreamingQuality {
	String getQualityLabel();
	int getResolutionHeight();
	String getBitrate();
	boolean isAvailable(String subscriptionPlan);
}
