package com.sda.project.patterns.composite;

import com.sda.project.model.Video;
import java.util.List;

public class VideoLeaf implements ContentComponent {
	private final Video video;

	public VideoLeaf(Video video) {
		this.video = video;
	}

	@Override
	public String getName() {
		return video.getTitle();
	}

	public Long getVideoId() {
		return video.getVideoId();
	}

	@Override
	public String getType() {
		return "video";
	}

	@Override
	public List<ContentComponent> getChildren() {
		return List.of();
	}

	@Override
	public void add(ContentComponent component) {
		throw new UnsupportedOperationException("Videos cannot contain children");
	}

	@Override
	public void remove(ContentComponent component) {
		throw new UnsupportedOperationException("Videos cannot contain children");
	}

	@Override
	public String display(int depth) {
		return "  ".repeat(depth) + "- " + video.getTitle() + System.lineSeparator();
	}
}
