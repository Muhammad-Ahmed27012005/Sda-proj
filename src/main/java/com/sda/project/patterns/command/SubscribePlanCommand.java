package com.sda.project.patterns.command;

import com.sda.project.model.enums.PlanName;
import com.sda.project.service.SubscriptionService;

public class SubscribePlanCommand implements Command {
	private final SubscriptionService subscriptionService;
	private final Long userId;
	private final PlanName planName;

	public SubscribePlanCommand(SubscriptionService subscriptionService, Long userId, PlanName planName) {
		this.subscriptionService = subscriptionService;
		this.userId = userId;
		this.planName = planName;
	}

	@Override
	public void execute() {
		subscriptionService.subscribe(userId, planName);
	}

	@Override
	public void undo() {
		subscriptionService.cancel(userId);
	}
}
