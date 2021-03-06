package com.example.galeria.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.galeria.R;
import com.example.galeria.models.Pagado;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    List<Pagado> list;
    Context context;

    public TicketAdapter(List<Pagado> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View ticketView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_ticket_products, viewGroup, false);
        TicketViewHolder tvh = new TicketViewHolder(ticketView);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder tiketViewHolder, int i) {
        double subtotal = Integer.parseInt(list.get(i).getUnits())*Double.parseDouble(list.get(i).getPrice());
        tiketViewHolder.name.setText(list.get(i).getName());
        tiketViewHolder.units.setText(list.get(i).getUnits()+ " x " + list.get(i).getPrice() + "€");
        tiketViewHolder.subtotal.setText(String.format("%.2f", subtotal) + "€");

        if(tiketViewHolder.name.getText().toString().length()>=15&& tiketViewHolder.name.getText().toString().length()<21) {
            tiketViewHolder.name.setTextSize(12);
        } else if (tiketViewHolder.name.getText().toString().length()>=21) {
            tiketViewHolder.name.setTextSize(11);
        }

        if(tiketViewHolder.units.getText().toString().length() == 10) {
            tiketViewHolder.units.setTextSize(13);
        } else if(tiketViewHolder.units.getText().toString().length()>=11){
            tiketViewHolder.units.setTextSize(12);
        }

        if(tiketViewHolder.subtotal.getText().toString().length() == 6) {
            tiketViewHolder.subtotal.setTextSize(13);
        } else if(tiketViewHolder.subtotal.getText().toString().length()>=7) {
            tiketViewHolder.subtotal.setTextSize(12);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView name, units, subtotal;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.idProduName);
            units = itemView.findViewById(R.id.idProduUnits);
            subtotal = itemView.findViewById(R.id.idProduSubtotal);
        }
    }
}
