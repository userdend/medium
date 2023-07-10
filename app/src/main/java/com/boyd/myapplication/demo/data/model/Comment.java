package com.boyd.myapplication.demo.data.model;

public class Comment {
    private String documentId;
    private String postId;
    private String userId;
    private String userImageUrl;
    private String name;
    private String creationDate;
    private String content;

    public Comment() {
    }

    public Comment(String documentId, String postId, String userId, String userImageUrl, String name, String creationDate, String content) {
        this.documentId = documentId;
        this.postId = postId;
        this.userId = userId;
        this.userImageUrl = userImageUrl;
        this.name = name;
        this.creationDate = creationDate;
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
