package com.sda.project.patterns.composite;

import java.util.ArrayList;
import java.util.List;

public class CategoryComposite implements ContentComponent {
	private final String name;
	private final List<ContentComponent> children = new ArrayList<>();

	public CategoryComposite(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return "category";
	}

	@Override
	public List<ContentComponent> getChildren() {
		return children;
	}

	@Override
	public void add(ContentComponent component) {
		children.add(component);
	}

	@Override
	public void remove(ContentComponent component) {
		children.remove(component);
	}

	@Override
	public String display(int depth) {
		StringBuilder builder = new StringBuilder("  ".repeat(depth)).append(name).append(System.lineSeparator());
		for (ContentComponent child : children) {
			builder.append(child.display(depth + 1));
		}
		return builder.toString();
	}
}
