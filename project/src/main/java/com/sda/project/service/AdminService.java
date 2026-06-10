package com.sda.project.service;

import com.sda.project.model.User;
import com.sda.project.model.enums.Role;
import com.sda.project.model.enums.SubscriptionStatus;
import com.sda.project.repository.PaymentRepository;
import com.sda.project.repository.SubscriptionRepository;
import com.sda.project.repository.UserRepository;
import com.sda.project.repository.VideoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
	private final UserRepository userRepository;
	private final VideoRepository videoRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final PaymentRepository paymentRepository;

	public AdminService(UserRepository userRepository, VideoRepository videoRepository, SubscriptionRepository subscriptionRepository, PaymentRepository paymentRepository) {
		this.userRepository = userRepository;
		this.videoRepository = videoRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.paymentRepository = paymentRepository;
	}

	public List<User> users() {
		return userRepository.findAll();
	}

	@Transactional
	public User blockUser(Long id) {
		User user = userRepository.findById(id).orElseThrow();
		user.setRole(Role.BLOCKED);
		return userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public Map<String, Object> dashboard() {
		BigDecimal revenue = paymentRepository.totalSuccessfulRevenue();
		return Map.of(
				"totalUsers", userRepository.count(),
				"totalVideos", videoRepository.count(),
				"activeSubscriptions", subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE),
				"revenue", revenue == null ? BigDecimal.ZERO : revenue);
	}

	public List<Object[]> revenueReport() {
		return paymentRepository.monthlyRevenue();
	}

	public Object mostWatched() {
		return videoRepository.findTrending(org.springframework.data.domain.PageRequest.of(0, 10));
	}
}
