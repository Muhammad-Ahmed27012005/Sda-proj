package com.sda.project.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sda.project.entity.Video;
import com.sda.project.repository.VideoRepository;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final FileStorageService fileStorageService;

    public VideoService(VideoRepository videoRepository, FileStorageService fileStorageService) {
        this.videoRepository = videoRepository;
        this.fileStorageService = fileStorageService;
    }

    public Video uploadVideo(MultipartFile videoFile, MultipartFile thumbnail, Video videoDetails) throws IOException {
        String videoPath = fileStorageService.storeFile(videoFile, "videos");
        String thumbPath = fileStorageService.storeFile(thumbnail, "thumbnails");
        Video video = new Video.VideoBuilder()
                .setTitle(videoDetails.getTitle())
                .setDescription(videoDetails.getDescription())
                .setGenre(videoDetails.getGenre())
                .setDuration(videoDetails.getDuration())
                .setReleaseYear(videoDetails.getReleaseYear())
                .setRating(videoDetails.getRating())
                .setThumbnailUrl(thumbPath)
                .setVideoUrl(videoPath)
                .build();
        return videoRepository.save(video);
    }

    public ResponseEntity<Resource> streamVideo(Long videoId, String rangeHeader) throws IOException {
        Video video = videoRepository.findById(videoId).orElseThrow();
        Path videoPath = fileStorageService.getFilePath(video.getVideoUrl());
        File videoFile = videoPath.toFile();
        long fileLength = videoFile.length();

        HttpHeaders headers = new HttpHeaders();
        if (rangeHeader == null) {
            headers.setContentType(MediaTypeFactory.getMediaType(videoFile.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM));
            headers.setContentLength(fileLength);
            return new ResponseEntity<>(new FileSystemResource(videoFile), headers, HttpStatus.OK);
        }

        // Parse Range: bytes=start-end
        String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileLength - 1;
        if (end >= fileLength) end = fileLength - 1;
        long contentLength = end - start + 1;

        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "r")) {
            raf.seek(start);
            byte[] buffer = new byte[(int) contentLength];
            raf.readFully(buffer);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(buffer));
            headers.setContentType(MediaTypeFactory.getMediaType(videoFile.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM));
            headers.setContentLength(contentLength);
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
            return new ResponseEntity<>(resource, headers, HttpStatus.PARTIAL_CONTENT);
        }
    }

    public Video getVideo(Long id) {
        return videoRepository.findById(id).orElseThrow();
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }
}