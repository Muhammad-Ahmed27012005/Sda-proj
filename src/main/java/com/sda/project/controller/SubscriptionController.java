package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.dto.PaymentResultDTO;
import com.sda.project.dto.SubscribeDTO;
import com.sda.project.model.Subscription;
import com.sda.project.patterns.command.CancelSubscriptionCommand;
import com.sda.project.patterns.command.SubscribePlanCommand;
import com.sda.project.patterns.command.VideoCommandInvoker;
import com.sda.project.service.PaymentService;
import com.sda.project.service.SubscriptionService;
import com.sda.project.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
	private final SubscriptionService subscriptionService;
	private final PaymentService paymentService;
	private final UserService userService;
	private final VideoCommandInvoker invoker;

	public SubscriptionController(SubscriptionService subscriptionService, PaymentService paymentService, UserService userService, VideoCommandInvoker invoker) {
		this.subscriptionService = subscriptionService;
		this.paymentService = paymentService;
		this.userService = userService;
		this.invoker = invoker;
	}

	@PostMapping("/subscribe")
	public ApiResponse<Map<String, Object>> subscribe(@Valid @RequestBody SubscribeDTO request, Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		PaymentResultDTO payment = paymentService.processSubscriptionPayment(userId, request.planName(), request.paymentMethod());
		// DESIGN PATTERN: Command
		invoker.executeCommand(new SubscribePlanCommand(subscriptionService, userId, request.planName()));
		return ApiResponse.ok("Subscription active", Map.of("payment", payment, "subscription", subscriptionService.activeSubscription(userId).orElseThrow()));
	}

	@PostMapping("/cancel")
	public ApiResponse<Void> cancel(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		// DESIGN PATTERN: Command
		invoker.executeCommand(new CancelSubscriptionCommand(subscriptionService, userId));
		return ApiResponse.ok("Subscription cancelled", null);
	}

	@GetMapping("/status")
	public ApiResponse<Map<String, Object>> status(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		Subscription subscription = subscriptionService.activeSubscription(userId).orElse(null);
		return ApiResponse.ok("Subscription status loaded", Map.of(
				"active", subscription != null,
				"subscription", subscription == null ? "none" : subscription));
	}
}
