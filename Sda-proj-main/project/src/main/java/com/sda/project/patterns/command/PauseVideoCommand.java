package com.sda.project.patterns.command;

public class PauseVideoCommand implements Command {
    private final VideoPlayerReceiver receiver;

    public PauseVideoCommand(VideoPlayerReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.pause();
    }
}
