package com.fekrah.customer.models;

import java.io.Serializable;

public class Driver implements Serializable {

    private String name;
    private String email;
    private String img;
    private String mobile;
    private String device_token;
    private String user_key;
    private int rating_count;
    private float rating;

    public Driver() {
    }

    public Driver(String name, String email, String img, String mobile, String device_token, String user_key, int rating_count, float rating) {
        this.name = name;
        this.email = email;
        this.img = img;
        this.mobile = mobile;
        this.device_token = device_token;
        this.user_key = user_key;
        this.rating_count = rating_count;
        this.rating = rating;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }
}
