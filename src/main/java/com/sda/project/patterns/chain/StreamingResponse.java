package com.sda.project.patterns.chain;

public record StreamingResponse(boolean allowed, String reason) {
	public static StreamingResponse allowed(String reason) {
		return new StreamingResponse(true, reason);
	}

	public static StreamingResponse denied(String reason) {
		return new StreamingResponse(false, reason);
	}
}
