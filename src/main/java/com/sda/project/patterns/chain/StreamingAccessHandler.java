package com.sda.project.patterns.chain;

public class StreamingAccessHandler extends StreamingRequestHandler {
	@Override
	public StreamingResponse handle(StreamingRequest request) {
		return StreamingResponse.allowed("Streaming access granted");
	}
}
