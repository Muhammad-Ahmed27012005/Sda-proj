package com.sda.project.controller;

import com.sda.project.entity.Video;
import com.sda.project.service.VideoService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
private final VideoService videoService;

public VideoController(VideoService videoService) {
this.videoService = videoService;
}

@PostMapping
public ResponseEntity<Video> uploadVideo(
@RequestParam("video") MultipartFile videoFile,
@RequestParam("thumbnail") MultipartFile thumbnail,
@ModelAttribute Video videoDetails) throws IOException {
return ResponseEntity.ok(videoService.uploadVideo(videoFile, thumbnail, videoDetails));
}

@GetMapping
public List<Video> getAllVideos() {
return videoService.getAllVideos();
}

@GetMapping("/{id}")
public Video getVideo(@PathVariable Long id) {
return videoService.getVideo(id);
}

@GetMapping("/{id}/stream")
public ResponseEntity<Resource> streamVideo(@PathVariable Long id,
@RequestHeader(value = "Range", required = false) String rangeHeader)
throws IOException {
return videoService.streamVideo(id, rangeHeader);
}
}