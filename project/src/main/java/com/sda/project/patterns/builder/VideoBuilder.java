package com.sda.project.patterns.builder;

import com.sda.project.model.Video;
import java.math.BigDecimal;

public class VideoBuilder {
	private Long videoId;
	private String title;
	private String description;
	private String genre;
	private String thumbnailUrl;
	private String videoUrl;
	private String imdbId;
	private Integer duration;
	private Integer releaseYear;
	private Double rating;

	public VideoBuilder videoId(Long id) {
		this.videoId = id;
		return this;
	}

	public VideoBuilder title(String value) {
		this.title = value;
		return this;
	}

	public VideoBuilder description(String value) {
		this.description = value;
		return this;
	}

	public VideoBuilder genre(String value) {
		this.genre = value;
		return this;
	}

	public VideoBuilder duration(Integer value) {
		this.duration = value;
		return this;
	}

	public VideoBuilder releaseYear(Integer value) {
		this.releaseYear = value;
		return this;
	}

	public VideoBuilder rating(Double value) {
		this.rating = value;
		return this;
	}

	public VideoBuilder thumbnailUrl(String value) {
		this.thumbnailUrl = value;
		return this;
	}

	public VideoBuilder videoUrl(String value) {
		this.videoUrl = value;
		return this;
	}

	public VideoBuilder imdbId(String value) {
		this.imdbId = value;
		return this;
	}

	public Video buildMovie() {
		this.genre = "Movie";
		return buildVideo();
	}

	public Video buildTvShow() {
		this.genre = "TV Show";
		return buildVideo();
	}

	public Video buildDocumentary() {
		this.genre = "Documentary";
		return buildVideo();
	}

	public Video buildVideo() {
		return Video.builder()
				.videoId(videoId)
				.title(title)
				.description(description)
				.genre(genre)
				.duration(duration)
				.releaseYear(releaseYear)
				.rating(rating == null ? null : BigDecimal.valueOf(rating))
				.thumbnailUrl(thumbnailUrl)
				.videoUrl(videoUrl)
				.imdbId(imdbId)
				.build();
	}
}
