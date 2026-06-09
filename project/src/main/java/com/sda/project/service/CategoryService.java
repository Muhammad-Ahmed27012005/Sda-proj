package com.sda.project.service;

import com.sda.project.model.Video;
import com.sda.project.patterns.composite.CategoryComposite;
import com.sda.project.patterns.composite.ContentComponent;
import com.sda.project.patterns.composite.VideoLeaf;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
	private final VideoService videoService;

	public CategoryService(VideoService videoService) {
		this.videoService = videoService;
	}

	public ContentComponent categoryTree() {
		// DESIGN PATTERN: Composite
		CategoryComposite entertainment = new CategoryComposite("Entertainment");
		CategoryComposite movies = new CategoryComposite("Movies");
		CategoryComposite tvShows = new CategoryComposite("TV Shows");
		CategoryComposite documentaries = new CategoryComposite("Documentaries");

		for (String name : List.of("Action", "Comedy", "Horror", "Adventure")) {
			movies.add(new CategoryComposite(name));
		}
		for (String name : List.of("Drama", "Sci-Fi", "Thriller")) {
			tvShows.add(new CategoryComposite(name));
		}

		for (Video video : videoService.findAll(null, null, null, null, 50)) {
			String genre = video.getGenre() == null ? "" : video.getGenre().toLowerCase();
			if (genre.contains("documentary")) {
				documentaries.add(new VideoLeaf(video));
			} else if (genre.contains("tv")) {
				tvShows.add(new VideoLeaf(video));
			} else {
				movies.add(new VideoLeaf(video));
			}
		}

		entertainment.add(movies);
		entertainment.add(tvShows);
		entertainment.add(documentaries);
		return entertainment;
	}
}
