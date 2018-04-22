package com.example.lahiru.trackapp;

/**
 * Created by Lahiru on 3/11/2018.
 */

public class Point {
    public double latitude;
    public double longitude;


    public Point() {
    }

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
