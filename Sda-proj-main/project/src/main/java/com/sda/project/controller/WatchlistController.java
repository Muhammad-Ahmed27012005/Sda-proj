package com.sda.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sda.project.entity.Watchlist;
import com.sda.project.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }
    @PostMapping("/{userId}/{videoId}")
    public ResponseEntity<String> addToWatchlist(@PathVariable Long userId, @PathVariable Long videoId) {
        watchlistService.addToWatchlist(userId, videoId);
        return ResponseEntity.ok("Added to watchlist");
    }

    @DeleteMapping("/{userId}/{videoId}")
    public ResponseEntity<String> removeFromWatchlist(@PathVariable Long userId, @PathVariable Long videoId) {
        watchlistService.removeFromWatchlist(userId, videoId);
        return ResponseEntity.ok("Removed from watchlist");
    }

    @GetMapping("/{userId}")
    public List<Watchlist> getUserWatchlist(@PathVariable Long userId) {
        return watchlistService.getUserWatchlist(userId);
    }
}