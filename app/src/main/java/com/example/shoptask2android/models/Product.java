package com.example.shoptask2android.models;

import java.util.Objects;

public class Product {
    private String id;

    private String name;
    private String amount;
    private String price;

    public Product() {}

    public Product(String id, String name, String amount, String price) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public String getId(){
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}