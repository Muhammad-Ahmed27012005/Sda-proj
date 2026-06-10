package com.sda.project.patterns.command;

import com.sda.project.service.WatchlistService;

public class AddToWatchlistCommand implements Command {
    private final WatchlistService watchlistService;
    private final Long userId;
    private final Long videoId;

    public AddToWatchlistCommand(WatchlistService watchlistService, Long userId, Long videoId) {
        this.watchlistService = watchlistService;
        this.userId = userId;
        this.videoId = videoId;
    }

    @Override
    public void execute() {
        watchlistService.addToWatchlist(userId, videoId);
    }
}
