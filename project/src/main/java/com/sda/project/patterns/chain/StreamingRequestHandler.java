package com.sda.project.patterns.chain;

public abstract class StreamingRequestHandler {
	protected StreamingRequestHandler next;

	public StreamingRequestHandler setNext(StreamingRequestHandler next) {
		this.next = next;
		return next;
	}

	public abstract StreamingResponse handle(StreamingRequest request);

	protected StreamingResponse handleNext(StreamingRequest request) {
		return next == null ? StreamingResponse.allowed("Streaming access granted") : next.handle(request);
	}
}
