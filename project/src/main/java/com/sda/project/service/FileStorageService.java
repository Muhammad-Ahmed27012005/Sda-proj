package com.sda.project.service;

import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.util.FileUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	private final FileUtil fileUtil;

	public FileStorageService(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	public String store(MultipartFile file, String folder) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename());
		String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
		String filename = UUID.randomUUID() + extension;
		Path targetDirectory = fileUtil.uploadRoot().resolve(folder).normalize();
		Path target = targetDirectory.resolve(filename).normalize();
		try {
			Files.createDirectories(targetDirectory);
			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
			return folder + "/" + filename;
		} catch (IOException ex) {
			throw new UncheckedIOException("Unable to store uploaded file", ex);
		}
	}

	public Resource load(String relativePath) {
		Path path = fileUtil.resolveUploadPath(relativePath);
		if (!Files.exists(path)) {
			throw new ResourceNotFoundException("Video file not found: " + relativePath);
		}
		return new FileSystemResource(path);
	}

	public void delete(String relativePath) {
		if (relativePath == null || relativePath.isBlank()) {
			return;
		}
		try {
			Files.deleteIfExists(fileUtil.resolveUploadPath(relativePath));
		} catch (IOException ex) {
			throw new UncheckedIOException("Unable to delete file", ex);
		}
	}
}
