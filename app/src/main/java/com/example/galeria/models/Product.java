package com.example.galeria.models;

public class Product {
    private int id;
    private String name;
    private double price;
    private int category_id;
    private String category;


    public Product(int id, String name, String price, int category_id, String category) {
        this.id = id;
        this.name = name;
        this.price = Double.parseDouble(price);
        this.category_id = category_id;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = Double.parseDouble(price);
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return id +", name= " + name + ", price= " + price + ", category= " + category;
    }
}
