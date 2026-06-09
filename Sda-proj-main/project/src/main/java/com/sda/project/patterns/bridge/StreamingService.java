package com.sda.project.patterns.bridge;

public abstract class StreamingService {
    protected final VideoQuality quality;

    protected StreamingService(VideoQuality quality) {
        this.quality = quality;
    }

    public abstract String stream(String title);
}
