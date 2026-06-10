package com.sda.project.service;

import com.sda.project.dto.PaymentRequest;
import com.sda.project.dto.PaymentResponse;
import com.sda.project.entity.Payment;
import com.sda.project.entity.Subscription;
import com.sda.project.entity.User;
import com.sda.project.exception.BadRequestException;
import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.repository.PaymentRepository;
import com.sda.project.repository.SubscriptionRepository;
import com.sda.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               PaymentRepository paymentRepository,
                               PaymentService paymentService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    public PaymentResponse subscribe(Long userId, Subscription.PlanName planName, PaymentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        PaymentResponse response = paymentService.processPayment(request);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(response.isSuccess() ? "SUCCESS" : "FAILED");
        payment.setTransactionId(response.getTransactionId());
        paymentRepository.save(payment);

        if (response.isSuccess()) {
            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setPlanName(planName);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusMonths(1));
            subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
        }

        return response;
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return subscriptionRepository.findByUser(user);
    }

    public List<String> getAvailablePlans() {
        return List.of("BASIC", "STANDARD", "PREMIUM");
    }
}
