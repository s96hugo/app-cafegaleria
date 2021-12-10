package com.example.galeria.models;

public class Order {
    private int id;
    private int user_id;
    private int ticket_id;


    public Order(int id, int id_user, int ticket_id) {
        this.id = id;
        this.user_id = user_id;
        this.ticket_id = ticket_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(int ticket_id) {
        this.ticket_id = ticket_id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", ticket_id=" + ticket_id +
                '}';
    }
}
