package com.sda.project.patterns.chain;

public class AgeRestrictionHandler extends StreamingHandler {
    @Override
    public void handle(StreamingRequest request) {
        Integer releaseYear = request.getVideo().getReleaseYear();
        if (releaseYear != null && releaseYear < 2000 && request.getUserAge() < 13) {
            throw new IllegalStateException("User does not meet the age requirement");
        }
        super.handle(request);
    }
}
