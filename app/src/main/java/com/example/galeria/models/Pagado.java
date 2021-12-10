package com.example.galeria.models;

import java.io.Serializable;

public class Pagado implements Serializable {
    private String name;
    private String unidades;
    private String price;

    public Pagado(String name, String unidades, String price) {
        this.name = name;
        this.unidades = unidades;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return unidades;
    }

    public void setUnits(String unidades) {
        this.unidades = unidades;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "pagado{" +
                "name='" + name + '\'' +
                ", units=" + unidades +
                ", price='" + price + '\'' +
                '}';
    }
}
