package com.sda.project.dto;

import lombok.Data;

@Data
public class VideoUploadRequest {
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private Integer releaseYear;
    private Double rating;
}