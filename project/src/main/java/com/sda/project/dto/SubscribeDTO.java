package com.sda.project.dto;

import com.sda.project.model.enums.PaymentMethod;
import com.sda.project.model.enums.PlanName;
import jakarta.validation.constraints.NotNull;

public record SubscribeDTO(@NotNull PlanName planName, @NotNull PaymentMethod paymentMethod) {
}
