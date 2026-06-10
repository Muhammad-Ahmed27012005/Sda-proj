package com.sda.project.patterns.command;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.entity.Subscription;
import com.sda.project.service.SubscriptionService;

public class SubscribePlanCommand implements Command {
    private final SubscriptionService subscriptionService;
    private final Long userId;
    private final Subscription.PlanName planName;
    private final PaymentRequest request;

    public SubscribePlanCommand(SubscriptionService subscriptionService,
                                Long userId,
                                Subscription.PlanName planName,
                                PaymentRequest request) {
        this.subscriptionService = subscriptionService;
        this.userId = userId;
        this.planName = planName;
        this.request = request;
    }

    @Override
    public void execute() {
        subscriptionService.subscribe(userId, planName, request);
    }
}
