package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.TicketAdapter;
import com.example.galeria.models.Category;
import com.example.galeria.models.Pagado;
import com.example.galeria.models.Product;
import com.example.galeria.models.ProductOrder;
import com.example.galeria.models.Ticket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketResumeActivity extends AppCompatActivity {

    TextView numeroTicket, table, total, cambio;
    Button cambiar, finalizar;
    ImageButton vuelta;
    double precio;

    RecyclerView mrv;

    SharedPreferences sharedPreferences;
    RequestQueue rq;

    TicketAdapter ticketAdapter;

    String mesa;
    Ticket currentTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_resume);

        numeroTicket = findViewById(R.id.idTicketResumeTvNumeroTicket);
        table = findViewById(R.id.idTicketResumeTvTable);
        total = findViewById(R.id.idTicketResumeTvTotal);
        cambiar = findViewById(R.id.idTicketResumeBEditar);
        finalizar = findViewById(R.id.idTicketResumeBFinalizar);
        mrv = findViewById(R.id.idTicketResumeRvProds);

        cambio = findViewById(R.id.tvVueltaa);
        vuelta = findViewById(R.id.ibVueltaa);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mesa = (String) getIntent().getSerializableExtra("pedido");
        currentTicket = (Ticket) getIntent().getSerializableExtra("ticket");
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);

        numeroTicket.setText("Ticket " + String.valueOf(currentTicket.getNumber()));
        table.setText(mesa);

        mrv.setHasFixedSize(true);
        mrv.setLayoutManager(new LinearLayoutManager(this));

        //Datos
        showBill(currentTicket);

        cambiar.setOnClickListener(view -> {
            Intent intent = new Intent(TicketResumeActivity.this, StateOrderActivity.class);
            intent.putExtra("ticket", currentTicket);
            intent.putExtra("pedido", mesa);
            intent.putExtra("datosBruto", (Serializable) getIntent().getSerializableExtra("datosBruto"));
            intent.putExtra("topProducts", (Serializable) getIntent().getSerializableExtra("topProducts"));
            intent.putExtra("products", (Serializable) getIntent().getSerializableExtra("products"));
            intent.putExtra("categories", (Serializable) getIntent().getSerializableExtra("categories"));
            intent.putExtra("abre", 1);
            startActivity(intent);

        });

        finalizar.setOnClickListener(view -> {
            showPaymentDialog(currentTicket.getId());
        });

        vuelta.setOnClickListener(view -> {
            showExchangeDialog();
        });
    }

    private void showPaymentDialog(int id) {
        final Dialog dialog = new Dialog(TicketResumeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.payment_dialog);

        Spinner s = dialog.findViewById(R.id.sTipoPago);
        Button b = dialog.findViewById(R.id.bPago);

        String[] payMethod = {"Efectivo","Tarjeta","Impago"};

        ArrayAdapter paymentAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, payMethod);
        paymentAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        s.setAdapter(paymentAdapter);

        dialog.show();

        b.setOnClickListener(view -> {
            makeBill(id, s.getSelectedItemPosition());
            dialog.dismiss();

        });
    }

    private void makeBill(int id, int tipoPago) {

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("pay", String.valueOf(tipoPago));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/tickets/" + id + "/cuenta",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                List<Pagado> prods = new ArrayList<>();

                                JSONObject ticketJS = response.getJSONObject("ticket");
                                Gson gson = new Gson();
                                Ticket ticket = gson.fromJson(ticketJS.toString(), Ticket.class);

                                JSONArray pagado = response.getJSONArray("unidades");
                                for(int i = 0; i< pagado.length();i++){
                                    JSONObject prod = pagado.getJSONObject(i);
                                    Pagado category = gson.fromJson(prod.toString(), Pagado.class);
                                    prods.add(category);
                                }


                                Intent intent = new Intent(TicketResumeActivity.this, TicketClosedActivity.class);
                                intent.putExtra("ticket", ticket);
                                intent.putExtra("mesa", (String) getIntent().getSerializableExtra("pedido"));
                                intent.putExtra("productos", (Serializable) prods);
                                intent.putExtra("call", 1);
                                startActivity(intent);


                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TicketResumeActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(TicketResumeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "No se ha realizado ninguna acción para evitar incoherencia con los datos", Toast.LENGTH_LONG).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexión o datos incorrectos", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(req);
    }

    private void showBill(Ticket ticket){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.HOME+"/tickets/" + ticket.getId() + "/showBill",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                List<Pagado> prods = new ArrayList<>();
                                Gson gson = new Gson();
                                JSONArray pagado = response.getJSONArray("unidades");
                                for(int i = 0; i< pagado.length();i++){
                                    JSONObject prod = pagado.getJSONObject(i);
                                    Pagado category = gson.fromJson(prod.toString(), Pagado.class);
                                    prods.add(category);
                                }

                                precio = 0;
                                for(Pagado prod : prods){
                                    precio += Integer.parseInt(prod.getUnits())*Double.parseDouble(prod.getPrice());
                                }
                                total.setText("Total: " + String.format("%.2f",precio) + "€");

                                ticketAdapter = new TicketAdapter(prods, TicketResumeActivity.this);
                                mrv.setAdapter(ticketAdapter);

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TicketResumeActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(TicketResumeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "No se ha realizado ninguna acción para evitar incoherencia con los datos", Toast.LENGTH_LONG).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexión o datos incorrectos", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(req);

    }

    private void showExchangeDialog() {
        final Dialog dialog = new Dialog(TicketResumeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.category_dialog);
        dialog.show();

        EditText tx = dialog.findViewById(R.id.etCategory);
        Button addd = dialog.findViewById(R.id.badd);
        tx.setHint("Calcular devolución");
        addd.setText("Calcula");

        addd.setOnClickListener(view -> {
            if(tx.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Introduzca cantidad pagada por el cliente", Toast.LENGTH_SHORT).show();

            } else {
                try{
                    cambio.setText("Entrega " + String.format("%.2f",Double.parseDouble(tx.getText().toString()))+ "€  ➝  Devolver " + String.format("%.2f",Double.parseDouble(tx.getText().toString()) - precio) + "€");
                    dialog.dismiss();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Formato incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TicketResumeActivity.this, OrderActivity.class);
        intent.putExtra("ticket", currentTicket);
        intent.putExtra("pedido", mesa);
        intent.putExtra("datosBruto", (Serializable) getIntent().getSerializableExtra("datosBruto"));
        intent.putExtra("topProducts", (Serializable) getIntent().getSerializableExtra("topProducts"));
        intent.putExtra("products", (Serializable) getIntent().getSerializableExtra("products"));
        intent.putExtra("categories", (Serializable) getIntent().getSerializableExtra("categories"));
        intent.putExtra("abre", 1);
        startActivity(intent);
    }
}