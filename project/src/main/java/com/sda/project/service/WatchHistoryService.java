package com.sda.project.service;

import com.sda.project.model.User;
import com.sda.project.model.Video;
import com.sda.project.model.WatchHistory;
import com.sda.project.repository.UserRepository;
import com.sda.project.repository.VideoRepository;
import com.sda.project.repository.WatchHistoryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WatchHistoryService {
	private final WatchHistoryRepository watchHistoryRepository;
	private final UserRepository userRepository;
	private final VideoRepository videoRepository;

	public WatchHistoryService(WatchHistoryRepository watchHistoryRepository, UserRepository userRepository, VideoRepository videoRepository) {
		this.watchHistoryRepository = watchHistoryRepository;
		this.userRepository = userRepository;
		this.videoRepository = videoRepository;
	}

	@Transactional
	public WatchHistory savePosition(Long userId, Long videoId, Integer lastPosition, BigDecimal percentage) {
		User user = userRepository.findById(userId).orElseThrow();
		Video video = videoRepository.findById(videoId).orElseThrow();
		WatchHistory history = watchHistoryRepository.findByUserAndVideo(user, video)
				.orElseGet(() -> WatchHistory.builder().user(user).video(video).build());
		history.setLastPosition(lastPosition == null ? 0 : lastPosition);
		history.setWatchPercentage(percentage == null ? BigDecimal.ZERO : percentage);
		history.setLastWatched(LocalDateTime.now());
		return watchHistoryRepository.save(history);
	}

	public int getLastPosition(Long userId, Long videoId) {
		User user = userRepository.findById(userId).orElseThrow();
		Video video = videoRepository.findById(videoId).orElseThrow();
		return watchHistoryRepository.findByUserAndVideo(user, video)
				.map(WatchHistory::getLastPosition)
				.orElse(0);
	}

	public List<WatchHistory> history(Long userId) {
		User user = userRepository.findById(userId).orElseThrow();
		return watchHistoryRepository.findByUserOrderByLastWatchedDesc(user);
	}

	public List<WatchHistory> continueWatching(Long userId) {
		User user = userRepository.findById(userId).orElseThrow();
		return watchHistoryRepository.findByUserAndWatchPercentageLessThanOrderByLastWatchedDesc(user, BigDecimal.valueOf(100));
	}

	@Transactional
	public void clear(Long userId, Long videoId) {
		User user = userRepository.findById(userId).orElseThrow();
		Video video = videoRepository.findById(videoId).orElseThrow();
		watchHistoryRepository.findByUserAndVideo(user, video).ifPresent(watchHistoryRepository::delete);
	}
}
