package com.example.galeria.adapters;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galeria.OrderActivity;
import com.example.galeria.R;
import com.example.galeria.TicketClosedActivity;
import com.example.galeria.models.Pagado;
import com.example.galeria.models.Table;
import com.example.galeria.models.Ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TicketClosedAdapter extends RecyclerView.Adapter<TicketClosedAdapter.TicketClosedViewHolder> {

    List<Ticket> tickets;
    List<Table> tables;
    Context context;

    public TicketClosedAdapter(List<Ticket> tickets, List<Table> tables, Context context) {
        this.tickets = tickets;
        this.tables = tables;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketClosedAdapter.TicketClosedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View ticketClosedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_tickets_closed, viewGroup, false);
        TicketClosedViewHolder tcvh = new TicketClosedViewHolder(ticketClosedView);
        return tcvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TicketClosedAdapter.TicketClosedViewHolder ticketClosedViewHolder, int i) {

        ticketClosedViewHolder.number.setText("Nº"+String.valueOf(tickets.get(i).getNumber()));

        String fecha [] = tickets.get(i).getDate().split(" ");
        String[] hora = fecha[1].split(":");
        ticketClosedViewHolder.fecha.setText(hora[0]+ ":" + hora[1]);
        ticketClosedViewHolder.total.setText(String.format("%.2f",tickets.get(i).getTotal()) + "€");


        ticketClosedViewHolder.cel.setOnClickListener(view -> {
            Intent intent = new Intent(context, TicketClosedActivity.class);
            intent.putExtra("ticket", tickets.get(i));
            intent.putExtra("mesa", calularMesa(tickets.get(i).getTable_id()));
            intent.putExtra("call", 0);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    private String calularMesa(int id_mesa){
        String mesa = "";

        switch (id_mesa){
            case 1:
                mesa = "barra 1";
                return  mesa;
            case 2:
                mesa = "barra 2";
                return  mesa;
            case 3:
                mesa = "barra 3";
                return  mesa;
            case 4:
                mesa = "barra 4";
                return  mesa;
            case 5:
                mesa = "mesa 1";
                return  mesa;
            case 6:
                mesa = "mesa 2";
                return  mesa;
            case 7:
                mesa = "mesa 3";
                return  mesa;
            case 8:
                mesa = "mesa 4";
                return  mesa;
            case 9:
                mesa = "mesa 5";
                return  mesa;
            case 10:
                mesa = "pasillo 1";
                return  mesa;
            case 11:
                mesa = "pasillo 2";
                return  mesa;
            case 12:
                mesa = "pasillo 3";
                return  mesa;
            case 13:
                mesa = "pasillo 4";
                return  mesa;
            case 14:
                mesa = "pasillo 5";
                return  mesa;
            default:
                mesa = "llevar";
                return  mesa;
        }

    }

    public class TicketClosedViewHolder extends RecyclerView.ViewHolder {

        TextView number, fecha, total;
        RelativeLayout cel;
        public TicketClosedViewHolder(@NonNull View itemView) {
            super(itemView);

            number = itemView.findViewById(R.id.idTicketNumber);
            fecha = itemView.findViewById(R.id.idTicketDate);
            total = itemView.findViewById(R.id.idTicketTotal);
            cel = itemView.findViewById(R.id.idLayoutTic);
        }
    }
}
