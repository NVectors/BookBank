package com.example.bookbank.models;

public class BookSearch {
    private String title;
    private String author;
    private String isbn;
    private String ownerName;
    private String status;
    private String imageUrl;

    public BookSearch() {
    }

    public BookSearch(String title, String author, String isbn, String ownerName, String status, String imageUrl) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.ownerName = ownerName;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
