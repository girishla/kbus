package com.bigmantra.natco.service;

import com.bigmantra.natco.models.User;

import java.util.ArrayList;

public class ProfileBuilder {
    private User user;
    private String userId;
    private ArrayList<byte[]> photoList;

    public ProfileBuilder() {

    }

    public User getUser() {
        return user;
    }

    public ProfileBuilder setUser(User user) {
        this.user = user;
        this.userId = user.getId();
        return this;
    }

    public ArrayList<byte[]> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<byte[]> photoList) {
        this.photoList = photoList;
    }

    public String getUserId() {
        return userId;
    }
}
