package com.sda.project.patterns.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryComposite extends ContentComponent {
    private final List<ContentComponent> children = new ArrayList<>();

    public CategoryComposite(String name) {
        super(name);
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
    public List<ContentComponent> getChildren() {
        return children;
    }

    @Override
    public String display() {
        return "Category: " + name + " [" + children.stream()
                .map(ContentComponent::display)
                .collect(Collectors.joining(", ")) + "]";
    }
}
