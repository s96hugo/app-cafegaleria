package com.example.galeria.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.galeria.R;
import com.example.galeria.interfaces.OnRefreshDataOrdered;
import com.example.galeria.models.Product;

import java.util.List;

public class ProductByCategoryAdapter extends RecyclerView.Adapter<ProductByCategoryAdapter.ProductsByCategoryViewHolder> {

    List<Product> products;
    Context context;
    OnRefreshDataOrdered orvl;

    public ProductByCategoryAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductByCategoryAdapter.ProductsByCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View productByCategoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_categories_order, viewGroup, false); //card_products_category
        ProductsByCategoryViewHolder cvh = new ProductsByCategoryViewHolder(productByCategoryView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductByCategoryAdapter.ProductsByCategoryViewHolder productsByCategoryViewHolder, int i) {
        if(products.get(i).getName().length() > 18){
            productsByCategoryViewHolder.tvProdName.setTextSize(12);
        }
        productsByCategoryViewHolder.tvProdName.setText(products.get(i).getName());
        productsByCategoryViewHolder.idCardProdCat.setOnClickListener(view -> {
            showProductOrderDialog(products.get(i));
        });

    }

    public void showProductOrderDialog(Product product){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.select_order_dialog);
        dialog.show();

        TextView nameDialog = dialog.findViewById(R.id.textView6);
        EditText comment = dialog.findViewById(R.id.idETComment);
        EditText unidades = dialog.findViewById(R.id.etunidades);
        ImageButton mas = dialog.findViewById(R.id.ibmas);
        ImageButton menos = dialog.findViewById(R.id.ibmenos);
        Button add = dialog.findViewById(R.id.baddorder);

        nameDialog.setText("Vas a añadir " + product.getName());
        unidades.setText("1");

        mas.setOnClickListener(view -> {
            int total = Integer.parseInt(unidades.getText().toString()) + 1;
            unidades.setText(String.valueOf(total));
        });

        menos.setOnClickListener(view -> {
            if(Integer.parseInt(unidades.getText().toString()) > 1) {
                int total = Integer.parseInt(unidades.getText().toString()) - 1;
                unidades.setText(String.valueOf(total));
            }

        });

        add.setOnClickListener(view -> {
            orvl = (OnRefreshDataOrdered)context;
            orvl.refreshData(product.getId(),
                    Integer.parseInt(unidades.getText().toString()),
                    comment.getText().toString().isEmpty() ? "" : comment.getText().toString(),
                    product.getName());
            dialog.dismiss();
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductsByCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvProdName;
        CardView idCardProdCat;
        public ProductsByCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProdName = itemView.findViewById(R.id.tvCatOrd);
            idCardProdCat = itemView.findViewById(R.id.idCardCatOrd);
            tvProdName.setTextSize(14);
        }
    }
}
