package com.sda.project.patterns.chain;

public class AuthenticationHandler extends StreamingHandler {
    @Override
    public void handle(StreamingRequest request) {
        if (!request.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated to stream video");
        }
        super.handle(request);
    }
}
