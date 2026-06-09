package com.sda.project.repository;

import com.sda.project.entity.Subscription;
import com.sda.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
    Optional<Subscription> findTopByUserOrderByEndDateDesc(User user);
}