package com.example.lahiru.trackapp;



public class Vehicle {
    private String vehi_num;
    private String vehi_model;
    private String image;

    public Vehicle() {
    }

    public Vehicle(String vehi_num, String vehi_model, String vehi_image) {
        this.vehi_num = vehi_num;
        this.vehi_model = vehi_model;
        this.image = vehi_image;
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

    public String getVehi_image() {
        return image;
    }

    public void setVehi_image(String vehi_image) {
        this.image = vehi_image;
    }
}

