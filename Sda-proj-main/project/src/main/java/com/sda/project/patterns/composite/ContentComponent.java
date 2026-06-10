package com.sda.project.patterns.composite;

import java.util.List;

public abstract class ContentComponent {
    protected final String name;

    protected ContentComponent(String name) {
        this.name = name;
    }

    public void add(ContentComponent component) {
        throw new UnsupportedOperationException("Cannot add child to this component");
    }

    public void remove(ContentComponent component) {
        throw new UnsupportedOperationException("Cannot remove child from this component");
    }

    public List<ContentComponent> getChildren() {
        throw new UnsupportedOperationException("This component has no children");
    }

    public abstract String display();
}
