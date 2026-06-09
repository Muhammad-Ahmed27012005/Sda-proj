package com.sda.project.dto;

import java.util.List;

public record ImdbVideoDTO(
		String id,
		String title,
		String plot,
		List<String> genres,
		Integer runtimeMinutes,
		Integer releaseYear,
		Double averageRating,
		String primaryImage) {
}
