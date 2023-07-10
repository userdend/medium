package com.boyd.myapplication.demo.data.model;

import androidx.annotation.Keep;

import java.util.List;

public class Posts {
    private String documentId;
    private String userId;
    private String userImageUrl;
    private String name;
    private String creationDate;
    private String content;
    private String imageContentUrl;
    private List<String> likes;
    private Long comments;

    @Keep
    public Posts() {
    }

    @Keep
    public Posts(String documentId, String userId, String userImageUrl, String name, String creationDate, String content, String imageContentUrl, List<String> likes, Long comments) {
        this.documentId = documentId;
        this.userId = userId;
        this.userImageUrl = userImageUrl;
        this.name = name;
        this.creationDate = creationDate;
        this.content = content;
        this.imageContentUrl = imageContentUrl;
        this.likes = likes;
        this.comments = comments;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    @Keep
    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    @Keep
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageContentUrl() {
        return imageContentUrl;
    }

    @Keep
    public void setImageContentUrl(String imageContentUrl) {
        this.imageContentUrl = imageContentUrl;
    }

    public List<String> getLikes() {
        return likes;
    }

    @Keep
    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }
}
