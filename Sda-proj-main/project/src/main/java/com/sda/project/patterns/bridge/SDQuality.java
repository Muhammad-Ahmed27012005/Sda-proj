package com.sda.project.patterns.bridge;

public class SDQuality implements VideoQuality {
    @Override
    public String getQualityLabel() {
        return "SD";
    }

    @Override
    public int getBitrateKbps() {
        return 1500;
    }
}
