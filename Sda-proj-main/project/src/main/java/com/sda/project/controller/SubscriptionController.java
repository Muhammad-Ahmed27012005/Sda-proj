package com.sda.project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.dto.PaymentResponse;
import com.sda.project.entity.Subscription;
import com.sda.project.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    @PostMapping("/{userId}/subscribe/{planName}")
    public PaymentResponse subscribe(@PathVariable Long userId,
                                     @PathVariable Subscription.PlanName planName,
                                     @RequestBody PaymentRequest request) {
        return subscriptionService.subscribe(userId, planName, request);
    }

    @GetMapping("/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }

    @GetMapping("/plans")
    public List<String> getAvailablePlans() {
        return subscriptionService.getAvailablePlans();
    }
}