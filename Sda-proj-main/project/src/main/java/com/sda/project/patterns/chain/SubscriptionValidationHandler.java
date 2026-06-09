package com.sda.project.patterns.chain;

public class SubscriptionValidationHandler extends StreamingHandler {
    @Override
    public void handle(StreamingRequest request) {
        if (!request.isSubscriptionActive()) {
            throw new IllegalStateException("Active subscription is required");
        }
        super.handle(request);
    }
}
