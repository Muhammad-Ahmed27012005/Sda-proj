package com.sda.project.service;

import com.sda.project.model.User;
import com.sda.project.model.Video;
import com.sda.project.model.Watchlist;
import com.sda.project.repository.UserRepository;
import com.sda.project.repository.VideoRepository;
import com.sda.project.repository.WatchlistRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WatchlistService {
	private final WatchlistRepository watchlistRepository;
	private final UserRepository userRepository;
	private final VideoRepository videoRepository;

	public WatchlistService(WatchlistRepository watchlistRepository, UserRepository userRepository, VideoRepository videoRepository) {
		this.watchlistRepository = watchlistRepository;
		this.userRepository = userRepository;
		this.videoRepository = videoRepository;
	}

	@Transactional
	public Watchlist add(Long userId, Long videoId) {
		User user = userRepository.findById(userId).orElseThrow();
		Video video = videoRepository.findById(videoId).orElseThrow();
		return watchlistRepository.findByUserAndVideo(user, video)
				.orElseGet(() -> watchlistRepository.save(Watchlist.builder().user(user).video(video).build()));
	}

	@Transactional
	public void remove(Long userId, Long videoId) {
		User user = userRepository.findById(userId).orElseThrow();
		Video video = videoRepository.findById(videoId).orElseThrow();
		watchlistRepository.deleteByUserAndVideo(user, video);
	}

	public List<Watchlist> getByUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow();
		return watchlistRepository.findByUserOrderByWatchlistIdDesc(user);
	}
}
