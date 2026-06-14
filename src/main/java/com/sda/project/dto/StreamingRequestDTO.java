package com.sda.project.dto;

public record StreamingRequestDTO(Long userId, Long videoId, boolean authenticated, String subscriptionStatus, Integer userAge) {
}
