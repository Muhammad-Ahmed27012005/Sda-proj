package com.sda.project.patterns.bridge;

public class NetflixStreamingService extends StreamingService {
    public NetflixStreamingService(VideoQuality quality) {
        super(quality);
    }

    @Override
    public String stream(String title) {
        return "Streaming " + title + " in " + quality.getQualityLabel()
                + " at " + quality.getBitrateKbps() + " kbps";
    }
}
