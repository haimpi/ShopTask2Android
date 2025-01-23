package com.example.shoptask2android.models;

public class Account {
    private String email;

    private String phone;

    public Account(){}

    public Account(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}