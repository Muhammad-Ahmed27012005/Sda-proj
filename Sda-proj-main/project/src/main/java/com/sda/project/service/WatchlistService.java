package com.sda.project.service;

import com.sda.project.entity.*;
import com.sda.project.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            UserRepository userRepository,
                            VideoRepository videoRepository) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public void addToWatchlist(Long userId, Long videoId) {
        User user = userRepository.findById(userId).orElseThrow();
        Video video = videoRepository.findById(videoId).orElseThrow();
        if (!watchlistRepository.existsByUserAndVideo(user, video)) {
            Watchlist item = new Watchlist();
            item.setUser(user);
            item.setVideo(video);
            watchlistRepository.save(item);
        }
    }

    public void removeFromWatchlist(Long userId, Long videoId) {
        User user = userRepository.findById(userId).orElseThrow();
        Video video = videoRepository.findById(videoId).orElseThrow();
        watchlistRepository.deleteByUserAndVideo(user, video);
    }

    public List<Watchlist> getUserWatchlist(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return watchlistRepository.findByUser(user);
    }
}