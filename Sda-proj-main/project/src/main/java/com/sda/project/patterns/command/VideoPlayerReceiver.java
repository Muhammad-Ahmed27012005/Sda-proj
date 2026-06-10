package com.sda.project.patterns.command;

public class VideoPlayerReceiver {
    private String currentVideo;

    public void play(String videoTitle) {
        currentVideo = videoTitle;
    }

    public void pause() {
        currentVideo = null;
    }

    public String getCurrentVideo() {
        return currentVideo;
    }
}
