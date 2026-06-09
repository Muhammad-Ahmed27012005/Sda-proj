package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.dto.StreamingResponseDTO;
import com.sda.project.dto.VideoDTO;
import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.exception.SubscriptionRequiredException;
import com.sda.project.model.Video;
import com.sda.project.patterns.command.PlayVideoCommand;
import com.sda.project.patterns.command.VideoCommandInvoker;
import com.sda.project.service.FileStorageService;
import com.sda.project.service.StreamingService;
import com.sda.project.service.UserService;
import com.sda.project.service.VideoService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
	private static final long CHUNK_SIZE = 1_048_576L;
	private final VideoService videoService;
	private final FileStorageService fileStorageService;
	private final StreamingService streamingService;
	private final UserService userService;
	private final VideoCommandInvoker invoker;

	public VideoController(VideoService videoService, FileStorageService fileStorageService, StreamingService streamingService, UserService userService, VideoCommandInvoker invoker) {
		this.videoService = videoService;
		this.fileStorageService = fileStorageService;
		this.streamingService = streamingService;
		this.userService = userService;
		this.invoker = invoker;
	}

	@GetMapping({"", "/"})
	public ApiResponse<List<VideoDTO>> list(
			@RequestParam(required = false) String genre,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) BigDecimal rating,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "50") int limit) {
		return ApiResponse.ok("Videos loaded", videoService.findAll(genre, year, rating, search, limit).stream().map(videoService::toDto).toList());
	}

	@GetMapping("/{id}")
	public ApiResponse<VideoDTO> details(@PathVariable Long id) {
		return ApiResponse.ok("Video loaded", videoService.toDto(videoService.findById(id)));
	}

	@GetMapping("/trending")
	public ApiResponse<List<VideoDTO>> trending() {
		return ApiResponse.ok("Trending videos loaded", videoService.trending().stream().map(videoService::toDto).toList());
	}

	@GetMapping("/recent")
	public ApiResponse<List<VideoDTO>> recent() {
		return ApiResponse.ok("Recent videos loaded", videoService.recent().stream().map(videoService::toDto).toList());
	}

	@PostMapping("/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<VideoDTO> upload(
			@RequestParam MultipartFile videoFile,
			@RequestParam(required = false) MultipartFile thumbnailFile,
			@RequestParam String title,
			@RequestParam(required = false) String description,
			@RequestParam(required = false, defaultValue = "Movie") String genre,
			@RequestParam(required = false) Integer duration,
			@RequestParam(required = false) Integer releaseYear,
			@RequestParam(required = false) BigDecimal rating,
			@RequestParam(required = false) String imdbId) {
		String videoPath = fileStorageService.store(videoFile, "videos");
		String thumbnailPath = fileStorageService.store(thumbnailFile, "thumbnails");
		VideoDTO dto = new VideoDTO(null, title, description, genre, duration, releaseYear, rating, thumbnailPath, videoPath, imdbId, null);
		return ApiResponse.ok("Video uploaded", videoService.toDto(videoService.createVideo(dto)));
	}

	@PostMapping("/imdb/{imdbId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<VideoDTO> addFromImdb(@PathVariable String imdbId) {
		return ApiResponse.ok("IMDb video imported", videoService.toDto(videoService.createFromImdb(imdbId)));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<VideoDTO> update(@PathVariable Long id, @Valid @RequestBody VideoDTO dto) {
		return ApiResponse.ok("Video updated", videoService.toDto(videoService.update(id, dto)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		Video video = videoService.findById(id);
		fileStorageService.delete(video.getVideoUrl());
		fileStorageService.delete(video.getThumbnailUrl());
		videoService.delete(id);
		return ApiResponse.ok("Video deleted", null);
	}

	@GetMapping("/stream-info/{id}")
	public ApiResponse<StreamingResponseDTO> streamInfo(@PathVariable Long id, Authentication authentication) {
		return ApiResponse.ok("Streaming info loaded", streamingService.validateAndStream(id, authentication));
	}

	@GetMapping(value = "/stream/{id}", produces = "video/mp4")
	public ResponseEntity<ResourceRegion> streamVideo(@PathVariable Long id, @RequestHeader HttpHeaders headers, Authentication authentication)
			throws IOException {
		StreamingResponseDTO response = streamingService.validateAndStream(id, authentication);
		if (!response.allowed()) {
			throw new SubscriptionRequiredException(response.reason());
		}
		Long userId = userService.currentUser(authentication).getUserId();
		// DESIGN PATTERN: Command
		invoker.executeCommand(new PlayVideoCommand(videoService, userId, id));
		Video video = videoService.findById(id);
		if (video.getVideoUrl() == null || video.getVideoUrl().isBlank()) {
			throw new ResourceNotFoundException("No local video file is attached to this title");
		}
		Resource resource = fileStorageService.load(video.getVideoUrl());
		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
				.contentType(MediaType.valueOf("video/mp4"))
				.body(region(resource, headers));
	}

	private ResourceRegion region(Resource resource, HttpHeaders headers) throws IOException {
		long contentLength = resource.contentLength();
		if (contentLength == 0) {
			return new ResourceRegion(resource, 0, 0);
		}
		List<HttpRange> ranges = headers.getRange();
		if (ranges.isEmpty()) {
			long rangeLength = Math.min(CHUNK_SIZE, contentLength);
			return new ResourceRegion(resource, 0, rangeLength);
		}
		HttpRange range = ranges.get(0);
		long start = range.getRangeStart(contentLength);
		long end = range.getRangeEnd(contentLength);
		long rangeLength = Math.min(CHUNK_SIZE, end - start + 1);
		return new ResourceRegion(resource, start, rangeLength);
	}
}
