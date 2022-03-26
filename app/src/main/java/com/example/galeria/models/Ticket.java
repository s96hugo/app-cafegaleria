package com.example.galeria.models;

import java.io.Serializable;
import java.util.Date;

public class Ticket implements Serializable {
    private int id;
    private String number;
    private String date;
    private double total;
    private String payment;
    private int table_id;

    /*
    public Ticket(int id, int number, int table_id) {
        this.id = id;
        this.number = number;
        this.table_id = table_id;
    }

     */

    public Ticket(int id, String number, String date, double total, String payment, int table_id) {
        this.id = id;
        this.number = number;
        this.date = date;
        this.total = total;
        this.payment = payment;
        this.table_id = table_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", number=" + number +
                ", date=" + date +
                ", total=" + total +
                ", payment='" + payment + '\'' +
                ", table_id=" + table_id +
                '}';
    }
}
