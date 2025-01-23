package com.example.shoptask2android.models;

public class Product {
    private String name;
    private String amount;
    private String price;

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(ShopItem.class)
    }

    public Product(String name, String amount, String price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getPrice() {
        return price;
    }
}
