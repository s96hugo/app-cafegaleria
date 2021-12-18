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

import com.example.galeria.CategoyActivity;
import com.example.galeria.R;
import com.example.galeria.interfaces.OnRefreshDataOrdered;
import com.example.galeria.models.Order;
import com.example.galeria.models.Product;
import com.example.galeria.models.ProductOrder;

import java.util.List;

public class OrderedAdapter extends RecyclerView.Adapter<OrderedAdapter.OrderedViewHolder> {

    List<ProductOrder> list;
    Context context;
    OnRefreshDataOrdered orvl;

    public OrderedAdapter(Context context, List<ProductOrder> list){
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public OrderedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View OrderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_selected_products, viewGroup, false);
        OrderedViewHolder ovh = new OrderedAdapter.OrderedViewHolder(OrderView);
        return ovh;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedViewHolder orderedAdapterViewHolder, int i) {
        orderedAdapterViewHolder.name.setText(list.get(i).getName());
        orderedAdapterViewHolder.cantidad.setText(String.valueOf(list.get(i).getUnits()));
        if(list.get(i).getComment().isEmpty()) {
            orderedAdapterViewHolder.info.setVisibility(View.INVISIBLE);
        }
        orderedAdapterViewHolder.mas.setOnClickListener(view -> {
            int total = list.get(i).getUnits();
            list.get(i).setUnits(total+1);
            orvl = (OnRefreshDataOrdered)context;
            orvl.refreshCurrent(list.get(i).getProduct_id(),
                    (list.get(i).getUnits()),
                    list.get(i).getComment(),
                    list.get(i).getName());
        });

        orderedAdapterViewHolder.menos.setOnClickListener(view -> {
            int total = list.get(i).getUnits();
            list.get(i).setUnits(total - 1);

            if((list.get(i).getUnits()) <= 0) {
                orvl = (OnRefreshDataOrdered) context;
                orvl.deleteProductOrdered(i);
            } else {

                orvl = (OnRefreshDataOrdered) context;
                orvl.refreshCurrent(list.get(i).getProduct_id(),
                        (list.get(i).getUnits()),
                        list.get(i).getComment(),
                        list.get(i).getName());
            }

        });

        orderedAdapterViewHolder.name.setOnClickListener(view -> {
            showCreateCommentDialog(list.get(i));
            if(list.get(i).getComment().isEmpty()) {
                orderedAdapterViewHolder.info.setVisibility(View.INVISIBLE);
            } else {
                orderedAdapterViewHolder.info.setVisibility(View.VISIBLE);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void showCreateCommentDialog(ProductOrder productOrder){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.category_dialog);
        dialog.show();

        EditText tx = dialog.findViewById(R.id.etCategory);
        Button addd = dialog.findViewById(R.id.badd);
        if (productOrder.getComment().equals("") || productOrder.getComment() == null) {
            tx.setHint("Comentario");
        } else {
            tx.setText(productOrder.getComment());
        }

        addd.setOnClickListener(view -> {
           // if(tx.getText().toString().isEmpty()){
                //dialog.dismiss();

            //} else {
                orvl = (OnRefreshDataOrdered)context;
                orvl.refreshCurrent(productOrder.getProduct_id(),
                        productOrder.getUnits(),
                        tx.getText().toString(),
                        productOrder.getName());
                dialog.dismiss();

            //}

        });
    }



    public class OrderedViewHolder extends  RecyclerView.ViewHolder{

        TextView name, cantidad;
        ImageButton mas, menos, info;
        LinearLayout lyTopProd;
        public OrderedViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.idProductOrdName);
            cantidad = itemView.findViewById(R.id.idProductQty);
            mas = itemView.findViewById(R.id.idPlusIcon);
            menos = itemView.findViewById(R.id.idMinusICon);
            info = itemView.findViewById(R.id.idCommentIcon);


        }
    }
}
