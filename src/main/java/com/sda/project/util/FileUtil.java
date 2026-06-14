package com.sda.project.util;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {
	private final Path uploadRoot;

	public FileUtil(@Value("${app.upload.dir}") String uploadDir) {
		this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
	}

	public Path uploadRoot() {
		return uploadRoot;
	}

	public Path resolveUploadPath(String relativePath) {
		return uploadRoot.resolve(relativePath).normalize();
	}
}
