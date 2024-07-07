package com.example.galeria;

public class Constant {
    public static final String URL =  "http://192.168.1.133:8081/"; //http://192.168.1.188:8081/;  // Local -> "http://192.168.1.133:8081/";
    public static final String HOME = URL+"api";

    //login
    public static final String LOGIN = HOME+"/login";
    public static final String REGISTER = HOME+"/register";
    public static final String LOGOUT = HOME+"/logout";
    public static final String GET_USERS = HOME+"/users";

    //Category
    public static final String CREATE_CATEGORY = HOME+"/categories/create";
    public static final String GET_ALL_CATEGORY = HOME+"/categories";
    //public static final String EDIT_CATEGORY = HOME+"/categories/update";

    //Product
    public static final String CREATE_PRODUCT = HOME+"/products/create";
    public static final String PRODUCTS_AND_CATEGORIES = HOME+"/products";
    public static final String TOP_PRODUCTS = HOME+"/products/popular";
    public static final String DATA_SET = HOME+"/productsDataSet";

    //tickets
    public static final String CREATE_TICKET = HOME+"/tickets/create";
    public static final String GET_CURRENT_TICKETS = HOME+"/ticketsOpen";
    public static final String GET_CLOSED_TICKETS = HOME+"/ticketsClosed";
    public static final String GET_BILLING = HOME+"/facturacion";
}