package com.sda.project.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class VideoStreamingUtil {
    public static void streamVideo(String filePath, HttpServletRequest request, HttpServletResponse response) {
        try {
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path);
            response.setContentType("video/mp4");
            long fileLength = Files.size(path);
            String rangeHeader = request.getHeader("Range");
            if (rangeHeader == null) {
                response.setContentLengthLong(fileLength);
                try (InputStream in = resource.getInputStream(); OutputStream out = response.getOutputStream()) {
                    in.transferTo(out);
                }
                return;
            }
            long start = Long.parseLong(rangeHeader.replace("bytes=", "").split("-")[0]);
            long end = Math.min(start + 1024 * 1024, fileLength - 1);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            response.setContentLengthLong(end - start + 1);
            try (InputStream in = Files.newInputStream(path); OutputStream out = response.getOutputStream()) {
                in.skip(start);
                byte[] buffer = new byte[4096];
                long remaining = end - start + 1;
                while (remaining > 0) {
                    int read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (read == -1) break;
                    out.write(buffer, 0, read);
                    remaining -= read;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Streaming error", e);
        }
    }
}