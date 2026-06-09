package com.sda.project.patterns.composite;

public class VideoLeaf extends ContentComponent {
    public VideoLeaf(String name) {
        super(name);
    }

    @Override
    public String display() {
        return "Video: " + name;
    }
}
