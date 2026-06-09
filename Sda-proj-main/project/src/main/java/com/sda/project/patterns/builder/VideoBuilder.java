package com.sda.project.patterns.builder;

import com.sda.project.entity.Video;

public class VideoBuilder {
    private final Video video = new Video();

    public VideoBuilder title(String title) {
        video.setTitle(title);
        return this;
    }

    public VideoBuilder description(String description) {
        video.setDescription(description);
        return this;
    }

    public VideoBuilder genre(String genre) {
        video.setGenre(genre);
        return this;
    }

    public VideoBuilder duration(Integer duration) {
        video.setDuration(duration);
        return this;
    }

    public VideoBuilder releaseYear(Integer releaseYear) {
        video.setReleaseYear(releaseYear);
        return this;
    }

    public VideoBuilder rating(Double rating) {
        video.setRating(rating);
        return this;
    }

    public VideoBuilder thumbnailUrl(String thumbnailUrl) {
        video.setThumbnailUrl(thumbnailUrl);
        return this;
    }

    public VideoBuilder videoUrl(String videoUrl) {
        video.setVideoUrl(videoUrl);
        return this;
    }

    public Video build() {
        return video;
    }
}
