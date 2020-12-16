package com.example.myproject;

public class PlaceModel {

    String title;
    String des;
    String address;
    String img;
    Double lat;
    Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public PlaceModel(String title, String des, String address, String img,Double lat,Double lng) {
        this.title = title;
        this.des = des;
        this.address = address;
        this.img = img;
        this.lat=lat;
        this.lng=lng;
    }
}
