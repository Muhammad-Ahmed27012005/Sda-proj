package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.dto.HistorySaveDTO;
import com.sda.project.patterns.command.PauseVideoCommand;
import com.sda.project.patterns.command.VideoCommandInvoker;
import com.sda.project.service.UserService;
import com.sda.project.service.WatchHistoryService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
	private final WatchHistoryService watchHistoryService;
	private final UserService userService;
	private final VideoCommandInvoker invoker;

	public HistoryController(WatchHistoryService watchHistoryService, UserService userService, VideoCommandInvoker invoker) {
		this.watchHistoryService = watchHistoryService;
		this.userService = userService;
		this.invoker = invoker;
	}

	@PostMapping("/save")
	public ApiResponse<Void> save(@Valid @RequestBody HistorySaveDTO request, Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		// DESIGN PATTERN: Command
		invoker.executeCommand(new PauseVideoCommand(
				watchHistoryService,
				userId,
				request.videoId(),
				request.lastPosition(),
				request.watchPercentage() == null ? BigDecimal.ZERO : request.watchPercentage()));
		return ApiResponse.ok("Playback position saved", null);
	}

	@GetMapping({"", "/"})
	public ApiResponse<List<Map<String, Object>>> history(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		return ApiResponse.ok("History loaded", watchHistoryService.history(userId).stream().map(item -> Map.<String, Object>of(
				"videoId", item.getVideo().getVideoId(),
				"title", item.getVideo().getTitle(),
				"lastPosition", item.getLastPosition(),
				"watchPercentage", item.getWatchPercentage(),
				"lastWatched", item.getLastWatched())).toList());
	}

	@GetMapping("/continue")
	public ApiResponse<List<Map<String, Object>>> continueWatching(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		return ApiResponse.ok("Continue watching loaded", watchHistoryService.continueWatching(userId).stream().map(item -> Map.<String, Object>of(
				"videoId", item.getVideo().getVideoId(),
				"title", item.getVideo().getTitle(),
				"lastPosition", item.getLastPosition(),
				"watchPercentage", item.getWatchPercentage())).toList());
	}
}
