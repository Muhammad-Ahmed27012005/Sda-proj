package com.sda.project.repository;

import com.sda.project.model.User;
import com.sda.project.model.Video;
import com.sda.project.model.WatchHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
	List<WatchHistory> findByUserOrderByLastWatchedDesc(User user);
	Optional<WatchHistory> findByUserAndVideo(User user, Video video);
	List<WatchHistory> findByUserAndWatchPercentageLessThanOrderByLastWatchedDesc(User user, java.math.BigDecimal percentage);
	long countByUser(User user);
}
