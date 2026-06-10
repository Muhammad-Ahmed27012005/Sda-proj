package com.sda.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VideoDTO(
		Long videoId,
		String title,
		String description,
		String genre,
		Integer duration,
		Integer releaseYear,
		BigDecimal rating,
		String thumbnailUrl,
		String videoUrl,
		String imdbId,
		LocalDateTime uploadDate) {
}
