package com.sda.project.patterns.bridge;

public record StreamingSession(Long videoId, Long userId, String qualityLabel, int resolutionHeight, String bitrate, String streamUrl) {
}
