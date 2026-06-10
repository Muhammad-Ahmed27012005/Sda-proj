package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.dto.VideoDTO;
import com.sda.project.dto.WatchlistDTO;
import com.sda.project.patterns.command.AddToWatchlistCommand;
import com.sda.project.patterns.command.RemoveFromWatchlistCommand;
import com.sda.project.patterns.command.VideoCommandInvoker;
import com.sda.project.service.UserService;
import com.sda.project.service.VideoService;
import com.sda.project.service.WatchlistService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
	private final WatchlistService watchlistService;
	private final UserService userService;
	private final VideoService videoService;
	private final VideoCommandInvoker invoker;

	public WatchlistController(WatchlistService watchlistService, UserService userService, VideoService videoService, VideoCommandInvoker invoker) {
		this.watchlistService = watchlistService;
		this.userService = userService;
		this.videoService = videoService;
		this.invoker = invoker;
	}

	@PostMapping("/add")
	public ApiResponse<Void> add(@Valid @RequestBody WatchlistDTO request, Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		// DESIGN PATTERN: Command
		invoker.executeCommand(new AddToWatchlistCommand(watchlistService, userId, request.videoId()));
		return ApiResponse.ok("Added to watchlist", null);
	}

	@DeleteMapping("/remove/{videoId}")
	public ApiResponse<Void> remove(@PathVariable Long videoId, Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		// DESIGN PATTERN: Command
		invoker.executeCommand(new RemoveFromWatchlistCommand(watchlistService, userId, videoId));
		return ApiResponse.ok("Removed from watchlist", null);
	}

	@GetMapping({"", "/"})
	public ApiResponse<List<VideoDTO>> list(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		List<VideoDTO> videos = watchlistService.getByUser(userId).stream()
				.map(item -> videoService.toDto(item.getVideo()))
				.toList();
		return ApiResponse.ok("Watchlist loaded", videos);
	}
}
