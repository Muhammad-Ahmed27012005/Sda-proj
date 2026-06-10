package com.sda.project.patterns.command;

public class PlayVideoCommand implements Command {
    private final VideoPlayerReceiver receiver;
    private final String videoTitle;

    public PlayVideoCommand(VideoPlayerReceiver receiver, String videoTitle) {
        this.receiver = receiver;
        this.videoTitle = videoTitle;
    }

    @Override
    public void execute() {
        receiver.play(videoTitle);
    }
}
