package com.sda.project.patterns.chain;

public class SubscriptionValidationHandler extends StreamingRequestHandler {
	@Override
	public StreamingResponse handle(StreamingRequest request) {
		if (!"ACTIVE".equalsIgnoreCase(request.getSubscriptionStatus())) {
			return StreamingResponse.denied("Active subscription required");
		}
		return handleNext(request);
	}
}
