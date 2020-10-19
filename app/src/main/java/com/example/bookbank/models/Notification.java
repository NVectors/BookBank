package com.example.bookbank.models;

public class Notification {
    private String id;
    private String message;

    public Notification() {
        // required for Firestore to be able to convert this object
    }

    public Notification(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
