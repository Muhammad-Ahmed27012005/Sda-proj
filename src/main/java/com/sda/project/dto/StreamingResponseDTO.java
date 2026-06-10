package com.sda.project.dto;

public record StreamingResponseDTO(
		boolean allowed,
		String reason,
		String streamUrl,
		String qualityLabel,
		Integer resolutionHeight,
		String bitrate) {
}
