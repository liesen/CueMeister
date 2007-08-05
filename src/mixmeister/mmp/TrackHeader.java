package mixmeister.mmp;

import java.util.Locale;

public class TrackHeader {
    private String archiveKey;
    private double bpm;
    private double masterVolume;
    private int trackType;
    private int mixingType;
    private double keyAdjustment;
    private int beatBlendType;

    /**
     * 
     *
     */
    public TrackHeader() {
        this("", 100.0, 1.0, TrackType.STANDARD, MixingType.STANDARD, 1.0, BeatBlendType.PRECISE_RHYTHM);
    }

    public TrackHeader(String archiveKey, double bpm, double masterVolume, int trackType, int mixingType, double keyAdjustment, int beatBlendType) {
        this.archiveKey = archiveKey;
        this.bpm = bpm;
        this.masterVolume = masterVolume;
        this.trackType = trackType;
        this.mixingType = mixingType;
        this.keyAdjustment = keyAdjustment;
        this.beatBlendType = beatBlendType;
    }

    public int getBeatBlendType() {
        return beatBlendType;
    }

    public void setBeatBlendType(int beatBlendType) {
        this.beatBlendType = beatBlendType;
    }

    public double getKeyAdjustment() {
        return keyAdjustment;
    }

    public void setKeyAdjustment(double keyAdjustment) {
        this.keyAdjustment = keyAdjustment;
    }

    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double masterVolume) {
        this.masterVolume = masterVolume;
    }

    public int getMixingType() {
        return mixingType;
    }

    public void setMixingType(int mixingType) {
        this.mixingType = mixingType;
    }

    public int getTrackType() {
        return trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
    }

    public String getArchiveKey() {
        return archiveKey;
    }

    public void setArchiveKey(String archiveKey) {
        this.archiveKey = archiveKey;
    }
    
    @Override
    public String toString() {
        return String.format(
            Locale.US, 
            "TrackHeader[bpm: %.2f, master volume: %.2f, type: %h, mixing: %h, key adj.: %.2f, beat blend: %h]",
            bpm, masterVolume, trackType, mixingType, keyAdjustment, beatBlendType);
    }
}
