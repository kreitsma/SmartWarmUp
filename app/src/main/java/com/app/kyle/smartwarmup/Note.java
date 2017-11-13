package com.app.kyle.smartwarmup;

/**
 * Created by Kyle on 6/4/2016.
 */
public class Note {
    private Integer pitch;
    private Integer duration;

    public Note(Integer pitch, Integer duration) {
        super();
        this.pitch = pitch;
        this.duration = duration;
    }

    public Integer getPitch() {
        return pitch;
    }

    public Integer getDuration() {
        return duration;
    }
}
