package com.example.bookbank.models;

public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String ownerId;
    private String borrowerId;
    private String status;

    public Book() {
        // required for Firestore to be able to convert this object
    }

    public Book(String id, String title, String author, String isbn, String description, String ownerId, String borrowerId,String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.ownerId = ownerId;
        this.borrowerId = borrowerId;
        this.status = status;
    }

    public Book( String title, String author, String isbn,String ownerId,String status  ) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
