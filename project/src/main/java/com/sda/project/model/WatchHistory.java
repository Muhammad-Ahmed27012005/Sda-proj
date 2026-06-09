package com.sda.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "watch_history", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
public class WatchHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long historyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "video_id", nullable = false)
	private Video video;

	@Column(name = "last_position")
	@Builder.Default
	private Integer lastPosition = 0;

	@Column(name = "watch_percentage")
	@Builder.Default
	private BigDecimal watchPercentage = BigDecimal.ZERO;

	@Column(name = "last_watched")
	@Builder.Default
	private LocalDateTime lastWatched = LocalDateTime.now();
}
