package com.sda.project.repository;

import com.sda.project.model.Subscription;
import com.sda.project.model.User;
import com.sda.project.model.enums.SubscriptionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	Optional<Subscription> findFirstByUserAndStatusOrderByEndDateDesc(User user, SubscriptionStatus status);
	List<Subscription> findByUserOrderByEndDateDesc(User user);
	long countByStatus(SubscriptionStatus status);
}
