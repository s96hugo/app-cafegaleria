package com.example.galeria.comparators;

import com.example.galeria.models.Product;

import java.util.Comparator;
import java.lang.Comparable;

public class ComparatorProduct implements Comparator<Product> {
    @Override
    public int compare(Product o, Product t1) {
        return o.compareTo(t1);
    }

}
