package com.sda.project.patterns.command;

import com.sda.project.service.WatchHistoryService;
import java.math.BigDecimal;

public class PauseVideoCommand implements Command {
	private final WatchHistoryService watchHistoryService;
	private final Long userId;
	private final Long videoId;
	private final Integer position;
	private final BigDecimal percentage;

	public PauseVideoCommand(WatchHistoryService watchHistoryService, Long userId, Long videoId, Integer position, BigDecimal percentage) {
		this.watchHistoryService = watchHistoryService;
		this.userId = userId;
		this.videoId = videoId;
		this.position = position;
		this.percentage = percentage;
	}

	@Override
	public void execute() {
		watchHistoryService.savePosition(userId, videoId, position, percentage);
	}

	@Override
	public void undo() {
		watchHistoryService.savePosition(userId, videoId, 0, BigDecimal.ZERO);
	}
}
