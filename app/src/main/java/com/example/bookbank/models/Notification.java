package com.example.bookbank.models;

public class Notification {
    private String id;
    private String userId;
    private String message;
    private String bookOwnerId;

    public Notification() {
        // required for Firestore to be able to convert this object
    }

    public Notification(String id, String userId, String message, String bookOwnerId) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.bookOwnerId = bookOwnerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBookOwnerId() {
        return bookOwnerId;
    }

    public void setBookOwnerId(String bookOwnerId) {
        this.bookOwnerId = bookOwnerId;
    }
}
