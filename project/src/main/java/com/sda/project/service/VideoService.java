package com.sda.project.service;

import com.sda.project.dto.ImdbVideoDTO;
import com.sda.project.dto.VideoDTO;
import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.model.Video;
import com.sda.project.patterns.builder.VideoBuilder;
import com.sda.project.patterns.singleton.ConfigurationManager;
import com.sda.project.patterns.singleton.DatabaseConnectionManager;
import com.sda.project.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoService {
	private static final Logger log = LoggerFactory.getLogger(VideoService.class);
	private final VideoRepository videoRepository;
	private final WatchHistoryService watchHistoryService;
	private final ImdbApiService imdbApiService;

	public VideoService(VideoRepository videoRepository, WatchHistoryService watchHistoryService, ImdbApiService imdbApiService) {
		this.videoRepository = videoRepository;
		this.watchHistoryService = watchHistoryService;
		this.imdbApiService = imdbApiService;
	}

	@PostConstruct
	void logConfiguration() {
		// DESIGN PATTERN: Singleton
		log.info("{}; max quality {}", DatabaseConnectionManager.getInstance().getConnectionStatus(), ConfigurationManager.getInstance().get("max.stream.quality"));
	}

	public List<Video> findAll(String genre, Integer year, BigDecimal rating, String search, int limit) {
		return videoRepository.search(blankToNull(genre), year, rating, blankToNull(search), PageRequest.of(0, Math.max(1, limit)));
	}

	public Video findById(Long id) {
		return videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
	}

	public List<Video> trending() {
		return videoRepository.findTrending(PageRequest.of(0, 10));
	}

	public List<Video> recent() {
		return videoRepository.findTop10ByOrderByUploadDateDesc();
	}

	@Transactional
	public Video createVideo(VideoDTO dto) {
		// DESIGN PATTERN: Builder
		Video video = new VideoBuilder()
				.title(dto.title())
				.description(dto.description())
				.genre(dto.genre())
				.duration(dto.duration())
				.releaseYear(dto.releaseYear())
				.rating(dto.rating() == null ? null : dto.rating().doubleValue())
				.thumbnailUrl(dto.thumbnailUrl())
				.videoUrl(dto.videoUrl())
				.imdbId(dto.imdbId())
				.buildVideo();
		return videoRepository.save(video);
	}

	@Transactional
	public Video createFromImdb(String imdbId) {
		return videoRepository.findByImdbId(imdbId).orElseGet(() -> {
			ImdbVideoDTO imdb = imdbApiService.fetchVideoById(imdbId);
			String genre = imdb.genres() == null || imdb.genres().isEmpty() ? "Movie" : String.join(", ", imdb.genres());
			VideoDTO dto = new VideoDTO(null, imdb.title(), imdb.plot(), genre, imdb.runtimeMinutes(), imdb.releaseYear(),
					imdb.averageRating() == null ? null : BigDecimal.valueOf(imdb.averageRating()), imdb.primaryImage(), null, imdb.id(), null);
			return createVideo(dto);
		});
	}

	@Transactional
	public Video update(Long id, VideoDTO dto) {
		Video video = findById(id);
		if (dto.title() != null) video.setTitle(dto.title());
		if (dto.description() != null) video.setDescription(dto.description());
		if (dto.genre() != null) video.setGenre(dto.genre());
		if (dto.duration() != null) video.setDuration(dto.duration());
		if (dto.releaseYear() != null) video.setReleaseYear(dto.releaseYear());
		if (dto.rating() != null) video.setRating(dto.rating());
		if (dto.thumbnailUrl() != null) video.setThumbnailUrl(dto.thumbnailUrl());
		if (dto.videoUrl() != null) video.setVideoUrl(dto.videoUrl());
		if (dto.imdbId() != null) video.setImdbId(dto.imdbId());
		return videoRepository.save(video);
	}

	@Transactional
	public void delete(Long id) {
		videoRepository.delete(findById(id));
	}

	public VideoDTO toDto(Video video) {
		return new VideoDTO(video.getVideoId(), video.getTitle(), video.getDescription(), video.getGenre(), video.getDuration(),
				video.getReleaseYear(), video.getRating(), video.getThumbnailUrl(), video.getVideoUrl(), video.getImdbId(), video.getUploadDate());
	}

	public void recordWatchStart(Long userId, Long videoId) {
		watchHistoryService.savePosition(userId, videoId, 0, BigDecimal.ZERO);
	}

	public void clearWatchStart(Long userId, Long videoId) {
		watchHistoryService.clear(userId, videoId);
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}
}
