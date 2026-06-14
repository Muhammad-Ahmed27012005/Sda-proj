package com.sda.project.service;

import com.sda.project.dto.VideoDTO;
import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.model.Video;
import com.sda.project.patterns.builder.VideoBuilder;
import com.sda.project.patterns.singleton.ConfigurationManager;
import com.sda.project.patterns.singleton.DatabaseConnectionManager;
import com.sda.project.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoService {
	private static final Logger log = LoggerFactory.getLogger(VideoService.class);
	private final VideoRepository videoRepository;
	private final WatchHistoryService watchHistoryService;
	private final Path moviesDirectory;

	public VideoService(
			VideoRepository videoRepository,
			WatchHistoryService watchHistoryService,
			@Value("${app.movies.dir:upload/movies}") String moviesDirectory) {
		this.videoRepository = videoRepository;
		this.watchHistoryService = watchHistoryService;
		this.moviesDirectory = Path.of(moviesDirectory).toAbsolutePath().normalize();
	}

	@PostConstruct
	void logConfiguration() {
		// DESIGN PATTERN: Singleton
		log.info("{}; max quality {}", DatabaseConnectionManager.getInstance().getConnectionStatus(), ConfigurationManager.getInstance().get("max.stream.quality"));
	}

	public List<Video> findAll(String genre, Integer year, BigDecimal rating, String search, int limit) {
		syncMoviesFolder();
		return videoRepository.search(blankToNull(genre), year, rating, blankToNull(search), PageRequest.of(0, Math.max(1, limit)))
				.stream()
				.filter(this::isLocalMovie)
				.collect(java.util.stream.Collectors.collectingAndThen(
						java.util.stream.Collectors.toMap(Video::getVideoUrl, video -> video, (first, ignored) -> first, LinkedHashMap::new),
						map -> List.copyOf(map.values())));
	}

	public Video findById(Long id) {
		syncMoviesFolder();
		return videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
	}

	public List<Video> trending() {
		syncMoviesFolder();
		return uniqueByPath(videoRepository.findTrending(PageRequest.of(0, 50))).stream().limit(10).toList();
	}

	public List<Video> recent() {
		syncMoviesFolder();
		return uniqueByPath(videoRepository.findAll()).stream()
				.sorted((left, right) -> right.getUploadDate().compareTo(left.getUploadDate()))
				.limit(10)
				.toList();
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

	@Transactional
	public void syncMoviesFolder() {
		try {
			Files.createDirectories(moviesDirectory);
			try (var files = Files.list(moviesDirectory)) {
				files.filter(Files::isRegularFile)
						.filter(this::isVideoFile)
						.forEach(this::upsertLocalMovie);
			}
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to scan movies folder: " + moviesDirectory, ex);
		}
	}

	private void upsertLocalMovie(Path file) {
		String relativePath = "movies/" + file.getFileName();
		if (videoRepository.findAll().stream().anyMatch(video -> relativePath.equals(video.getVideoUrl()))) {
			return;
		}
		String title = titleFromFilename(file.getFileName().toString());
		// Look for a matching thumbnail image (same stem) in upload/thumbnails
		String thumbnailPath = findMatchingThumbnail(file);
		VideoDTO dto = new VideoDTO(
				null,
				title,
				"Local streaming asset discovered from the movies folder.",
				"Movie",
				null,
				null,
				null,
				thumbnailPath,
				relativePath,
				null,
				null);
		createVideo(dto);
	}

	/**
	 * Looks for thumbnails/<stem>.jpg|png|webp alongside the video file.
	 * Returns a relative path like "thumbnails/mymovie.jpg" or null if none found.
	 */
	private String findMatchingThumbnail(Path videoFile) {
		String stem = videoFile.getFileName().toString();
		int dot = stem.lastIndexOf('.');
		if (dot > 0) stem = stem.substring(0, dot);
		Path thumbDir = moviesDirectory.getParent().resolve("thumbnails");
		for (String ext : new String[]{".jpg", ".jpeg", ".png", ".webp"}) {
			Path candidate = thumbDir.resolve(stem + ext);
			if (java.nio.file.Files.exists(candidate)) {
				return "thumbnails/" + stem + ext;
			}
		}
		return null;
	}

	private boolean isVideoFile(Path file) {
		String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
		return name.endsWith(".mp4") || name.endsWith(".webm") || name.endsWith(".mov") || name.endsWith(".m4v");
	}

	private boolean isLocalMovie(Video video) {
		return video.getVideoUrl() != null && video.getVideoUrl().startsWith("movies/");
	}

	private List<Video> uniqueByPath(List<Video> videos) {
		Map<String, Video> byPath = new LinkedHashMap<>();
		for (Video video : videos) {
			if (isLocalMovie(video)) {
				byPath.putIfAbsent(video.getVideoUrl(), video);
			}
		}
		return List.copyOf(byPath.values());
	}

	private String titleFromFilename(String filename) {
		int dot = filename.lastIndexOf('.');
		String base = dot > 0 ? filename.substring(0, dot) : filename;
		return base.replace('_', ' ').replace('-', ' ').replaceAll("\\s+", " ").trim();
	}
}
