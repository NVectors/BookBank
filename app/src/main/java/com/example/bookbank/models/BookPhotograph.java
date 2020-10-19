package com.example.bookbank.models;

public class BookPhotograph {
    private String id;
    private String imageSourceUrl;
    private String bookId;

    public BookPhotograph() {
        // required for Firestore to be able to convert this object
    }

    public BookPhotograph(String id, String imageSourceUrl, String bookId) {
        this.id = id;
        this.imageSourceUrl = imageSourceUrl;
        this.bookId = bookId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageSourceUrl() {
        return imageSourceUrl;
    }

    public void setImageSourceUrl(String imageSourceUrl) {
        this.imageSourceUrl = imageSourceUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
