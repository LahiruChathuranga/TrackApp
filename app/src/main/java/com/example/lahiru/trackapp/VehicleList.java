package com.example.lahiru.trackapp;



public class VehicleList {
    private String vehi_num;
    private String vehi_model;
    private String lat;
    private String lng;
    private String distance;
    private String date;

    public VehicleList() {
    }

    public VehicleList(String vehi_num, String vehi_model, String lat, String lng, String distance, String date) {
        this.vehi_num = vehi_num;
        this.vehi_model = vehi_model;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.date = date;
    }

    public String getVehi_num() {
        return vehi_num;
    }

    public void setVehi_num(String vehi_num) {
        this.vehi_num = vehi_num;
    }

    public String getVehi_model() {
        return vehi_model;
    }

    public void setVehi_model(String vehi_model) {
        this.vehi_model = vehi_model;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
