package com.sda.project.patterns.bridge;

public class HDQuality implements VideoQuality {
    @Override
    public String getQualityLabel() {
        return "HD";
    }

    @Override
    public int getBitrateKbps() {
        return 3500;
    }
}
