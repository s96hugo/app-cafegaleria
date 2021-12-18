package com.example.galeria.models;


public class Product implements Comparable<Product> {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id == product.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        //return id +", name= " + name + ", price= " + price + ", category= " + category;
        return this.getName();
    }

    @Override
    public int compareTo(Product o) {
        if(this.getId()>o.getId()){
            return 1;
        } else if(this.getId()<getId()){
            return -1;
        } else {
            return 0;
        }
    }
}
