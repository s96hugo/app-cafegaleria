package com.example.galeria.interfaces;

public interface OnRefreshDataOrdered {
    public void refreshData(int product_id, int units, String comment, String name);
    public void refreshCurrent(int product_id, int units, String comment, String name);
    public void deleteProductOrdered(int position);
}
