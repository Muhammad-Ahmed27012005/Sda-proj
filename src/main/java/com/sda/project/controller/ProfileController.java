package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.model.User;
import com.sda.project.service.SubscriptionService;
import com.sda.project.service.UserService;
import com.sda.project.service.WatchHistoryService;
import com.sda.project.service.WatchlistService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	private final UserService userService;
	private final SubscriptionService subscriptionService;
	private final WatchlistService watchlistService;
	private final WatchHistoryService watchHistoryService;

	public ProfileController(UserService userService,
			SubscriptionService subscriptionService,
			WatchlistService watchlistService,
			WatchHistoryService watchHistoryService) {
		this.userService = userService;
		this.subscriptionService = subscriptionService;
		this.watchlistService = watchlistService;
		this.watchHistoryService = watchHistoryService;
	}

	/** Full profile data: user info + stats */
	@GetMapping
	public ApiResponse<Map<String, Object>> get(Authentication authentication) {
		User user = userService.currentUser(authentication);
		Long userId = user.getUserId();

		int watchlistCount = watchlistService.getByUser(userId).size();
		int historyCount   = watchHistoryService.history(userId).size();

		boolean hasSub = subscriptionService.hasActiveSubscription(userId);
		String planName = hasSub
				? subscriptionService.activeSubscription(userId)
						.map(s -> s.getPlanName().name()).orElse("FREE")
				: "FREE";
		String subEndDate = hasSub
				? subscriptionService.activeSubscription(userId)
						.map(s -> s.getEndDate().toString()).orElse(null)
				: null;
		String subStatus = hasSub ? "ACTIVE" : "NONE";

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("userId",         user.getUserId());
		data.put("fullName",       user.getFullName());
		data.put("email",          user.getEmail());
		data.put("role",           user.getRole().name());
		data.put("profileImage",   user.getProfileImage());
		data.put("createdAt",      user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
		data.put("planName",       planName);
		data.put("subEndDate",     subEndDate);
		data.put("subStatus",      subStatus);
		data.put("watchlistCount", watchlistCount);
		data.put("historyCount",   historyCount);

		return ApiResponse.ok("Profile loaded", data);
	}

	/** Update name and/or profile photo */
	@PutMapping
	public ApiResponse<Map<String, Object>> update(
			@RequestParam(required = false) String fullName,
			@RequestParam(required = false) MultipartFile image,
			Authentication authentication) {
		User user = userService.currentUser(authentication);
		User updated = userService.updateProfile(user.getUserId(), fullName, image);

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("userId",       updated.getUserId());
		data.put("fullName",     updated.getFullName());
		data.put("email",        updated.getEmail());
		data.put("role",         updated.getRole().name());
		data.put("profileImage", updated.getProfileImage());

		return ApiResponse.ok("Profile updated", data);
	}
}
