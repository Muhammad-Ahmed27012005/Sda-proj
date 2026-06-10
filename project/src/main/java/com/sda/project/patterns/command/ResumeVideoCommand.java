package com.sda.project.patterns.command;

import com.sda.project.service.WatchHistoryService;

public class ResumeVideoCommand implements Command {
	private final WatchHistoryService watchHistoryService;
	private final Long userId;
	private final Long videoId;
	private int lastPosition;

	public ResumeVideoCommand(WatchHistoryService watchHistoryService, Long userId, Long videoId) {
		this.watchHistoryService = watchHistoryService;
		this.userId = userId;
		this.videoId = videoId;
	}

	@Override
	public void execute() {
		lastPosition = watchHistoryService.getLastPosition(userId, videoId);
	}

	@Override
	public void undo() {
		lastPosition = 0;
	}

	public int getLastPosition() {
		return lastPosition;
	}
}
