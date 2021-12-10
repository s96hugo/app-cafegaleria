package com.example.galeria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.galeria.models.Pagado;
import com.example.galeria.models.Product;
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

public class TicketClosedActivity extends AppCompatActivity {

    TextView numero, fecha, mesa, tipo, total;
    Button back;
    ImageButton vuelta;
    Ticket currentTicket;
    List<Pagado> list;
    TicketAdapter ticketAdapter;
    private RecyclerView mrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_closed);

        //Enlazar con el layout
        numero = findViewById(R.id.tvNumticket);
        fecha = findViewById(R.id.tvFecha);
        mesa =findViewById(R.id.tvTable);
        tipo = findViewById(R.id.tvTipoPago);
        total = findViewById(R.id.tvTotal);
        back = findViewById(R.id.bvolver);
        vuelta = findViewById(R.id.ibVuelta);

        //Vuelta es el botón del cambio, si al cargar los datos del ticket se ve que es en efectivo se vuelve visible.
        vuelta.setVisibility(View.INVISIBLE);

        mrv = (RecyclerView) findViewById(R.id.idRecyclerViewTicketProd);
        mrv.setHasFixedSize(true);
        mrv.setLayoutManager(new LinearLayoutManager(this));

        currentTicket = (Ticket) getIntent().getSerializableExtra("ticket");
        String nMesa = (String) getIntent().getSerializableExtra("mesa");

        numero.setText("Ticket " + String.valueOf(currentTicket.getNumber()));
        fecha.setText("Fecha: "+ currentTicket.getDate());
        mesa.setText(nMesa);
        tipo.setText("Pagado " + currentTicket.getPayment().toLowerCase());
        total.setText("Total: " + String.format("%.2f",currentTicket.getTotal()) + "€");

        if(currentTicket.getPayment().equals("en efectivo")) vuelta.setVisibility(View.VISIBLE);


        // Ver quien llama a la clase
        int mode = (int) getIntent().getSerializableExtra("call");
        if(mode == 1){ //1 si es al realizar la cuenta en la clase OrderActivity

            list = (List<Pagado>) getIntent().getSerializableExtra("productos");
            ticketAdapter = new TicketAdapter(list, TicketClosedActivity.this);
            mrv.setAdapter(ticketAdapter);

            back.setOnClickListener(view -> {
                Intent intent = new Intent(TicketClosedActivity.this, MainActivity.class);
                startActivity(intent);
            });


        } else { //0 si es al llamarlo desde TicketClosedAdapter

            RequestQueue rq = Volley.newRequestQueue(this);
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                    Constant.HOME+"/tickets/" + currentTicket.getId() + "/showBill",
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

                                    //Una vez cargado los pedidos de ese ticket, mostrarlos en el adaptador
                                    ticketAdapter = new TicketAdapter(prods, TicketClosedActivity.this);
                                    mrv.setAdapter(ticketAdapter);


                                } else {

                                    if(!response.getBoolean("token")){
                                        Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(TicketClosedActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
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


            back.setOnClickListener(view -> {
                Intent intent = new Intent(TicketClosedActivity.this, TicketActivity.class);
                startActivity(intent);
            });

        }

    }

    @Override
    public void onBackPressed() {

        //Ver quien llama a la clase
        int mode = (int) getIntent().getSerializableExtra("call");
        if(mode == 1){ //1 si es al realizar la cuenta en la clase OrderActivity
            Intent intent = new Intent(TicketClosedActivity.this, MainActivity.class);
            startActivity(intent);

        } else { //0 si es al llamarlo desde TicketClosedAdapter
            Intent intent = new Intent(TicketClosedActivity.this, TicketActivity.class);
            startActivity(intent);
        }

    }
}