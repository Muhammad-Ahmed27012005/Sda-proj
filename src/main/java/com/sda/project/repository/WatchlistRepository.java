package com.sda.project.repository;

import com.sda.project.model.User;
import com.sda.project.model.Video;
import com.sda.project.model.Watchlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
	List<Watchlist> findByUserOrderByWatchlistIdDesc(User user);
	Optional<Watchlist> findByUserAndVideo(User user, Video video);
	boolean existsByUserAndVideo(User user, Video video);
	void deleteByUserAndVideo(User user, Video video);
	long countByUser(User user);
}
