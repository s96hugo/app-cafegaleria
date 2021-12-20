package com.example.galeria.models;

import java.io.Serializable;

public class Pagado implements Serializable {
    private int id;
    private String name;
    private String unidades;
    private String price;

    public Pagado(int id, String name, String unidades, String price) {
        this.id = id;
        this.name = name;
        this.unidades = unidades;
        this.price = price;
    }

    public int getId(){
        return this.id;
    }

    public void setID(int id){
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pagado pagado = (Pagado) o;

        return id == pagado.id;
    }

    @Override
    public int hashCode() {
        return id;
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
