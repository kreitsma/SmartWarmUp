package com.app.kyle.smartwarmup;

/**
 * Created by Kyle on 6/2/2016.
 */
public class Warmup {
    private String filename;
    private String startNote;
    private Integer startInterval;
    private Integer numIntervals;

    public Warmup(String filename, String startNote, Integer startInterval, Integer numIntervals) {
        super();
        this.filename = filename;
        this.startNote = startNote;
        this.startInterval = startInterval;
        this.numIntervals = numIntervals;
    }

    public String getFilename() {
        return filename;
    }
    public String getStartNote() {
        return startNote;
    }
    public Integer getStartInterval() { return startInterval; }
    public Integer getNumIntervals() {
        return numIntervals;
    }
}
