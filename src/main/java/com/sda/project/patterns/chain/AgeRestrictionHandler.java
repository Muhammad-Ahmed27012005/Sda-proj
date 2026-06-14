package com.sda.project.patterns.chain;

public class AgeRestrictionHandler extends StreamingRequestHandler {
	@Override
	public StreamingResponse handle(StreamingRequest request) {
		Integer userAge = request.getUserAge() == null ? 18 : request.getUserAge();
		if (userAge < 13) {
			return StreamingResponse.denied("Age restriction prevents this stream");
		}
		return handleNext(request);
	}
}
