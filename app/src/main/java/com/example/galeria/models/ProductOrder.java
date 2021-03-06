package com.example.galeria.models;

import java.io.Serializable;

public class ProductOrder implements Serializable {
    private int id;
    private int units;
    private String comment;
    private String name;
    private int product_id;
    private int order_id;

    public ProductOrder(int id, int units, String comment, String name, int product_id, int order_id) {
        this.id = id;
        this.units = units;
        this.comment = comment;
        this.name = name;
        this.product_id = product_id;
        this.order_id = order_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductOrder that = (ProductOrder) o;

        if (id != that.id) return false;
        if (product_id != that.product_id) return false;
        return order_id == that.order_id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + product_id;
        result = 31 * result + order_id;
        return result;
    }

    @Override
    public String toString() {
        String salida = "";
        salida = comment.equals("") || comment == null ? name : name + "  -  " + comment;
        return  units + " x " + salida;
    }


}
