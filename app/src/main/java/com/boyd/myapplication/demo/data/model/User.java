package com.boyd.myapplication.demo.data.model;

import androidx.annotation.Keep;

public class User {
    int id;
    String documentId;
    String name;
    String userId;
    String userImg;

    public User(int id, String documentId, String name, String userId, String userImg) {
        this.id = id;
        this.documentId = documentId;
        this.name = name;
        this.userId = userId;
        this.userImg = userImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImg() {
        return userImg;
    }

    @Keep
    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
