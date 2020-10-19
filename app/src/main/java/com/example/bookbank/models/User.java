package com.example.bookbank.models;

public class User {
    private String id;
    private String email;
    private String password;
    private String fullname;
    private String address;
    private String phoneNumber;

    public User() {
        // required for Firestore to be able to convert this object
    }

    public User(String id, String email, String password, String fullname, String address, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
