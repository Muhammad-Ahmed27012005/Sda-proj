package com.sda.project.controller;

import com.sda.project.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final VideoService videoService;

    public AdminController(VideoService videoService) {
        this.videoService = videoService;
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok("Video deleted");
    }
}