package com.sda.project.patterns.bridge;

public abstract class StreamingServiceBridge {
	protected final StreamingQuality quality;

	protected StreamingServiceBridge(StreamingQuality quality) {
		this.quality = quality;
	}

	public abstract StreamingSession startStream(Long videoId, Long userId);
}
