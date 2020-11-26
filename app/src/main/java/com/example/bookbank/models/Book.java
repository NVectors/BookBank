package com.example.bookbank.models;

public class Book {
    private String id;
    private String title;
    private String author;
    private Long isbn;
    private String description;
    private String ownerId;
    private String borrowerId;
    private String status;
    private boolean borrowerScanReturn;
    private boolean ownerScanReturn;
    private Boolean ownerScanHandOver;

    public Book() {
        // required for Firestore to be able to convert this object
    }

    public Book(String id, String title, String author, long isbn, String description, String status, String ownerId, String borrowerId, Boolean borrowerScanReturn, Boolean ownerScanReturn, Boolean ownerScanHandOver) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.ownerId = ownerId;
        this.borrowerId = borrowerId;
        this.status = status;
        //Boolean borrowerScanReturn, Boolean ownerScanReturn
        this.borrowerScanReturn = borrowerScanReturn;
        this.ownerScanReturn = ownerScanReturn;
        this.ownerScanHandOver = ownerScanHandOver;
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

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
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

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOwnerScanReturn() {
        return ownerScanReturn;
    }

    public void setOwnerScanReturn0(boolean ownerScanReturn) {
        this.ownerScanReturn = ownerScanReturn;
    }

    public boolean isBorrowerScanReturn() {
        return borrowerScanReturn;
    }

    public void setBorrowerScanReturn(boolean borrowerScanReturn) {
        this.borrowerScanReturn = borrowerScanReturn;
    }

    public Boolean getOwnerScanHandOver() {
        return ownerScanHandOver;
    }

    public void setOwnerScanReturn(boolean ownerScanHandOver) {
        this.ownerScanHandOver = ownerScanHandOver;
    }

}
