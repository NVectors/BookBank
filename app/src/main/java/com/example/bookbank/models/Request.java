package com.example.bookbank.models;

public class Request {
    private String id;
    private String bookId;
    private String bookTitle;
    private String requesterId;
    private String ownerId;
    private String status;
    private Double latitude;
    private Double longitude;

    public Request() {
        // required for Firestore to be able to convert this object
    }

    public Request(String id, String bookId, String bookTitle, String requesterId, String ownerId, String status, Double latitude, Double longitude) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() { return bookTitle; }

    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
