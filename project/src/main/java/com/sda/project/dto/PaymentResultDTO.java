package com.sda.project.dto;

import com.sda.project.model.enums.PaymentStatus;
import java.math.BigDecimal;

public record PaymentResultDTO(
		boolean success,
		String transactionId,
		String message,
		BigDecimal amount,
		PaymentStatus status) {
}
