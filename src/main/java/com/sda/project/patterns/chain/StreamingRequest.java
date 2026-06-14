package com.sda.project.patterns.chain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamingRequest {
	private Long userId;
	private Long videoId;
	private boolean authenticated;
	private String subscriptionStatus;
	private Integer userAge;
}
