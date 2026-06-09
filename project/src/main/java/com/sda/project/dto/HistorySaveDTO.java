package com.sda.project.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record HistorySaveDTO(@NotNull Long videoId, Integer lastPosition, BigDecimal watchPercentage) {
}
