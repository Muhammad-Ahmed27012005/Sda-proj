package com.sda.project.service;

import com.sda.project.dto.StreamingResponseDTO;
import com.sda.project.exception.SubscriptionRequiredException;
import com.sda.project.model.Subscription;
import com.sda.project.model.enums.PlanName;
import com.sda.project.patterns.bridge.FullHDQuality;
import com.sda.project.patterns.bridge.HDQuality;
import com.sda.project.patterns.bridge.SDQuality;
import com.sda.project.patterns.bridge.StreamingQuality;
import com.sda.project.patterns.bridge.StreamingSession;
import com.sda.project.patterns.bridge.UltraHDQuality;
import com.sda.project.patterns.bridge.VideoStreamingService;
import com.sda.project.patterns.chain.AgeRestrictionHandler;
import com.sda.project.patterns.chain.AuthenticationHandler;
import com.sda.project.patterns.chain.StreamingAccessHandler;
import com.sda.project.patterns.chain.StreamingRequest;
import com.sda.project.patterns.chain.StreamingResponse;
import com.sda.project.patterns.chain.SubscriptionValidationHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class StreamingService {
	private final UserService userService;
	private final SubscriptionService subscriptionService;

	public StreamingService(UserService userService, SubscriptionService subscriptionService) {
		this.userService = userService;
		this.subscriptionService = subscriptionService;
	}

	public StreamingResponseDTO validateAndStream(Long videoId, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
			return new StreamingResponseDTO(true, "Public local library stream", "/api/videos/stream/" + videoId, "Local", 1080, "Adaptive");
		}
		Long userId = userService.currentUser(authentication).getUserId();
		Subscription subscription = subscriptionService.activeSubscription(userId)
				.orElseThrow(() -> new SubscriptionRequiredException("Subscribe to a plan before streaming"));
		StreamingRequest request = StreamingRequest.builder()
				.userId(userId)
				.videoId(videoId)
				.authenticated(authentication != null && authentication.isAuthenticated())
				.subscriptionStatus(subscription.getStatus().name())
				.userAge(18)
				.build();
		AuthenticationHandler auth = new AuthenticationHandler();
		SubscriptionValidationHandler subscriptionHandler = new SubscriptionValidationHandler();
		AgeRestrictionHandler age = new AgeRestrictionHandler();
		StreamingAccessHandler access = new StreamingAccessHandler();
		auth.setNext(subscriptionHandler).setNext(age).setNext(access);
		// DESIGN PATTERN: Chain of Responsibility
		StreamingResponse response = auth.handle(request);
		if (!response.allowed()) {
			return new StreamingResponseDTO(false, response.reason(), null, null, null, null);
		}
		StreamingQuality quality = qualityFor(subscription.getPlanName());
		// DESIGN PATTERN: Bridge
		StreamingSession session = new VideoStreamingService(quality).startStream(videoId, userId);
		return new StreamingResponseDTO(true, response.reason(), session.streamUrl(), session.qualityLabel(), session.resolutionHeight(), session.bitrate());
	}

	private StreamingQuality qualityFor(PlanName planName) {
		return switch (planName) {
			case BASIC -> new SDQuality();
			case STANDARD -> new HDQuality();
			case PREMIUM -> new UltraHDQuality().isAvailable(planName.name()) ? new UltraHDQuality() : new FullHDQuality();
		};
	}
}
