package com.sda.project.patterns.composite;

import java.util.List;

public interface ContentComponent {
	String getName();
	String getType();
	List<ContentComponent> getChildren();
	void add(ContentComponent component);
	void remove(ContentComponent component);
	String display(int depth);
}
