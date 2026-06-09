package com.sda.project.patterns.command;

import com.sda.project.service.SubscriptionService;

public class CancelSubscriptionCommand implements Command {
	private final SubscriptionService subscriptionService;
	private final Long userId;

	public CancelSubscriptionCommand(SubscriptionService subscriptionService, Long userId) {
		this.subscriptionService = subscriptionService;
		this.userId = userId;
	}

	@Override
	public void execute() {
		subscriptionService.cancel(userId);
	}

	@Override
	public void undo() {
		subscriptionService.subscribe(userId, com.sda.project.model.enums.PlanName.BASIC);
	}
}
