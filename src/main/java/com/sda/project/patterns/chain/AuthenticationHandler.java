package com.sda.project.patterns.chain;

public class AuthenticationHandler extends StreamingRequestHandler {
	@Override
	public StreamingResponse handle(StreamingRequest request) {
		if (!request.isAuthenticated()) {
			return StreamingResponse.denied("Authentication required");
		}
		return handleNext(request);
	}
}
