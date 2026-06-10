package com.sda.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private Integer releaseYear;
    private Double rating;
    private String thumbnailUrl;
    private String videoUrl;
    @CreationTimestamp
    private LocalDateTime uploadDate;

    // Builder pattern
    private Video(VideoBuilder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.genre = builder.genre;
        this.duration = builder.duration;
        this.releaseYear = builder.releaseYear;
        this.rating = builder.rating;
        this.thumbnailUrl = builder.thumbnailUrl;
        this.videoUrl = builder.videoUrl;
    }

    public static class VideoBuilder {
        private String title;
        private String description;
        private String genre;
        private Integer duration;
        private Integer releaseYear;
        private Double rating;
        private String thumbnailUrl;
        private String videoUrl;

        public VideoBuilder setTitle(String title) { this.title = title; return this; }
        public VideoBuilder setDescription(String description) { this.description = description; return this; }
        public VideoBuilder setGenre(String genre) { this.genre = genre; return this; }
        public VideoBuilder setDuration(Integer duration) { this.duration = duration; return this; }
        public VideoBuilder setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; return this; }
        public VideoBuilder setRating(Double rating) { this.rating = rating; return this; }
        public VideoBuilder setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; return this; }
        public VideoBuilder setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; return this; }
        public Video build() { return new Video(this); }
    }
}