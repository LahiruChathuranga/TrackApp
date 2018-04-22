package com.example.lahiru.trackapp;

/**
 * Created by Lahiru on 3/18/2018.
 */

public class Distance {
    private long date;
    private String distance;

    public Distance() {
    }

    public Distance(long date, String distance) {
        this.date = date;
        this.distance = distance;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
