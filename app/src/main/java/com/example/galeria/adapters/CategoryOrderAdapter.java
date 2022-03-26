package com.example.galeria.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galeria.R;
import com.example.galeria.interfaces.OpenProductsByCategory;
import com.example.galeria.models.Category;
import com.example.galeria.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryOrderAdapter extends RecyclerView.Adapter<CategoryOrderAdapter.CategoryViewHolder> {

    private OpenProductsByCategory opbc;
    List<Category> categories;
    List<Product> products;
    List<Product> productsFiltered;
    Context context;

    public CategoryOrderAdapter(List<Category> categories, List<Product> products, Context context) {
        this.categories = categories;
        this.products = products;
        this.productsFiltered = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_categories_order, viewGroup, false);
        CategoryViewHolder cvh = new CategoryViewHolder(categoryView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {
        if(categories.get(i).getCategory().length() > 18){
            categoryViewHolder.idCateName.setTextSize(12);
        }
        categoryViewHolder.idCateName.setText(categories.get(i).toString());
        categoryViewHolder.cel.setOnClickListener(view -> {
            productsFiltered.clear();
            //productsFiltered = products.stream().filter(c -> c.getCategory_id() == categories.get(i).getId()).collect(Collectors.toList());
            for(Product p : products){
                if (p.getCategory_id() ==categories.get(i).getId()){
                    productsFiltered.add(p);
                }
            }

            opbc = (OpenProductsByCategory)context;
            opbc.productsByCategory(productsFiltered);


        });


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView idCateName;
        LinearLayout cel;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            idCateName = itemView.findViewById(R.id.tvCatOrd);
            idCateName.setTextSize(15);
            cel = itemView.findViewById(R.id.idLatoutCatOrd);
        }
    }
}
