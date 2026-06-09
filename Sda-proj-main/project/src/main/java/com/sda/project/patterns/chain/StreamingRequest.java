package com.sda.project.patterns.chain;

import com.sda.project.entity.Video;

public class StreamingRequest {
    private final Video video;
    private final boolean authenticated;
    private final boolean subscriptionActive;
    private final int userAge;

    public StreamingRequest(Video video, boolean authenticated, boolean subscriptionActive, int userAge) {
        this.video = video;
        this.authenticated = authenticated;
        this.subscriptionActive = subscriptionActive;
        this.userAge = userAge;
    }

    public Video getVideo() {
        return video;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isSubscriptionActive() {
        return subscriptionActive;
    }

    public int getUserAge() {
        return userAge;
    }
}
