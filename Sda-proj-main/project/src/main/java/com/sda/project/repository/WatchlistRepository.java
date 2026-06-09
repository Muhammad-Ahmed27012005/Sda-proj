package com.sda.project.repository;

import com.sda.project.entity.User;
import com.sda.project.entity.Video;
import com.sda.project.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    boolean existsByUserAndVideo(User user, Video video);
    void deleteByUserAndVideo(User user, Video video);
    List<Watchlist> findByUser(User user);
}
