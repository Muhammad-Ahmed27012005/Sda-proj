package com.sda.project.dto;

import jakarta.validation.constraints.NotNull;

public record WatchlistDTO(@NotNull Long videoId) {
}
