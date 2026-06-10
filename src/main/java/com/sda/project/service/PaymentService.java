package com.sda.project.service;

import com.sda.project.dto.PaymentResultDTO;
import com.sda.project.model.Payment;
import com.sda.project.model.User;
import com.sda.project.model.enums.PaymentMethod;
import com.sda.project.model.enums.PaymentStatus;
import com.sda.project.model.enums.PlanName;
import com.sda.project.patterns.adapter.PayPalAdapter;
import com.sda.project.patterns.adapter.PayPalGateway;
import com.sda.project.patterns.adapter.PaymentGateway;
import com.sda.project.patterns.adapter.PaymentResult;
import com.sda.project.patterns.adapter.StripeAdapter;
import com.sda.project.patterns.adapter.StripeGateway;
import com.sda.project.repository.PaymentRepository;
import com.sda.project.repository.UserRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final SubscriptionService subscriptionService;

	public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, SubscriptionService subscriptionService) {
		this.paymentRepository = paymentRepository;
		this.userRepository = userRepository;
		this.subscriptionService = subscriptionService;
	}

	@Transactional
	public PaymentResultDTO processSubscriptionPayment(Long userId, PlanName planName, PaymentMethod method) {
		User user = userRepository.findById(userId).orElseThrow();
		BigDecimal amount = subscriptionService.priceFor(planName);
		// DESIGN PATTERN: Adapter
		PaymentGateway gateway = gatewayFor(method);
		PaymentResult result = gateway.processPayment(amount.doubleValue(), "USD", String.valueOf(userId));
		Payment payment = Payment.builder()
				.user(user)
				.amount(amount)
				.paymentMethod(method)
				.paymentStatus(result.success() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
				.transactionId(result.transactionId())
				.build();
		paymentRepository.save(payment);
		return new PaymentResultDTO(result.success(), result.transactionId(), result.message(), amount, payment.getPaymentStatus());
	}

	private PaymentGateway gatewayFor(PaymentMethod method) {
		return switch (method) {
			case PAYPAL -> new PayPalAdapter(new PayPalGateway());
			case STRIPE, DEMO -> new StripeAdapter(new StripeGateway());
		};
	}
}
