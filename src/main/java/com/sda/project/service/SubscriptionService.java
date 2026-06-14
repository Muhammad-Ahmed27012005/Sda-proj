package com.sda.project.service;

import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.model.Subscription;
import com.sda.project.model.User;
import com.sda.project.model.enums.PlanName;
import com.sda.project.model.enums.SubscriptionStatus;
import com.sda.project.repository.SubscriptionRepository;
import com.sda.project.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;

	public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
		this.subscriptionRepository = subscriptionRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public Subscription subscribe(Long userId, PlanName planName) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		subscriptionRepository.findFirstByUserAndStatusOrderByEndDateDesc(user, SubscriptionStatus.ACTIVE)
				.ifPresent(active -> {
					active.setStatus(SubscriptionStatus.CANCELLED);
					subscriptionRepository.save(active);
				});
		Subscription subscription = Subscription.builder()
				.user(user)
				.planName(planName)
				.startDate(LocalDate.now())
				.endDate(LocalDate.now().plusMonths(1))
				.status(SubscriptionStatus.ACTIVE)
				.build();
		return subscriptionRepository.save(subscription);
	}

	@Transactional
	public void cancel(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		subscriptionRepository.findFirstByUserAndStatusOrderByEndDateDesc(user, SubscriptionStatus.ACTIVE)
				.ifPresent(subscription -> {
					subscription.setStatus(SubscriptionStatus.CANCELLED);
					subscriptionRepository.save(subscription);
				});
	}

	public Optional<Subscription> activeSubscription(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return subscriptionRepository.findFirstByUserAndStatusOrderByEndDateDesc(user, SubscriptionStatus.ACTIVE)
				.filter(subscription -> !subscription.getEndDate().isBefore(LocalDate.now()));
	}

	public boolean hasActiveSubscription(Long userId) {
		return activeSubscription(userId).isPresent();
	}

	public String planFor(Long userId) {
		return activeSubscription(userId).map(subscription -> subscription.getPlanName().name()).orElse("BASIC");
	}

	public BigDecimal priceFor(PlanName planName) {
		return switch (planName) {
			case BASIC -> BigDecimal.valueOf(7.99);
			case STANDARD -> BigDecimal.valueOf(11.99);
			case PREMIUM -> BigDecimal.valueOf(15.99);
		};
	}
}
