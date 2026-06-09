package com.sda.project.patterns.bridge;

public class FullHDQuality implements VideoQuality {
    @Override
    public String getQualityLabel() {
        return "Full HD";
    }

    @Override
    public int getBitrateKbps() {
        return 6000;
    }
}
