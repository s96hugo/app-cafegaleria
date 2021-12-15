package com.example.galeria.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galeria.R;
import com.example.galeria.interfaces.OnRefreshDataOrdered;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.models.Product;

import java.util.List;

public class MostPopularAdapter extends RecyclerView.Adapter<MostPopularAdapter.MostPopularViewHolder> {

    List<Product> topProducts;
    Context context;
    OnRefreshDataOrdered orvl;

    public MostPopularAdapter(Context context, List<Product> list){
        this.context = context;
        this.topProducts = list;
    }


    @NonNull
    @Override
    public MostPopularViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View MostPopularView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_top_products, viewGroup, false);
        MostPopularViewHolder mpvh = new MostPopularAdapter.MostPopularViewHolder(MostPopularView);
        return mpvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MostPopularViewHolder mostPopularViewHolder, int i) {
        mostPopularViewHolder.name.setText(topProducts.get(i).getName());
        mostPopularViewHolder.category.setText(topProducts.get(i).getCategory());
        mostPopularViewHolder.price.setText(String.format("%.2f",topProducts.get(i).getPrice())+"€");

        mostPopularViewHolder.lyTopProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductOrderDialog(topProducts.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return topProducts.size();
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

    public class MostPopularViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, category;
        CardView lyTopProd;
        public MostPopularViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvProdName);
            category = itemView.findViewById(R.id.tvProdCategory);
            price = itemView.findViewById(R.id.tvPrice);
            lyTopProd = itemView.findViewById(R.id.idCardTop);
        }
    }
}
