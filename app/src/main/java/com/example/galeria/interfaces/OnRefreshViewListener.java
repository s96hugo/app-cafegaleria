package com.example.galeria.interfaces;

import com.example.galeria.models.Category;
import com.example.galeria.models.Product;
import com.example.galeria.models.User;

import java.util.List;

public interface OnRefreshViewListener {

    public void refreshView();
    public void refreshCategory(List<Category> categories);
    public void refreshProduct(List<Product> products, List<Category> categories);
    public void refreshUsers(List<User> users);
}
