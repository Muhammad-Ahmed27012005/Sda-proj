package com.sda.project.patterns.command;

import com.sda.project.service.VideoService;

public class PlayVideoCommand implements Command {
	private final VideoService videoService;
	private final Long userId;
	private final Long videoId;

	public PlayVideoCommand(VideoService videoService, Long userId, Long videoId) {
		this.videoService = videoService;
		this.userId = userId;
		this.videoId = videoId;
	}

	@Override
	public void execute() {
		videoService.recordWatchStart(userId, videoId);
	}

	@Override
	public void undo() {
		videoService.clearWatchStart(userId, videoId);
	}
}
