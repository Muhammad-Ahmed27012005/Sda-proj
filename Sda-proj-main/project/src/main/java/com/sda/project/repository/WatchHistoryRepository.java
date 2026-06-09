package com.sda.project.repository;

import com.sda.project.entity.User;
import com.sda.project.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUserOrderByLastWatchedDesc(User user);
}