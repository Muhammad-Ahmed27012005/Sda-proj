package com.sda.project.patterns.bridge;

public class VideoStreamingService extends StreamingServiceBridge {
	public VideoStreamingService(StreamingQuality quality) {
		super(quality);
	}

	@Override
	public StreamingSession startStream(Long videoId, Long userId) {
		return new StreamingSession(
				videoId,
				userId,
				quality.getQualityLabel(),
				quality.getResolutionHeight(),
				quality.getBitrate(),
				"/api/videos/stream/" + videoId);
	}
}
