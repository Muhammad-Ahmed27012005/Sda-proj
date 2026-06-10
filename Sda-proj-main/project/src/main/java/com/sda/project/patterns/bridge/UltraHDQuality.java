package com.sda.project.patterns.bridge;

public class UltraHDQuality implements VideoQuality {
    @Override
    public String getQualityLabel() {
        return "Ultra HD";
    }

    @Override
    public int getBitrateKbps() {
        return 16000;
    }
}
