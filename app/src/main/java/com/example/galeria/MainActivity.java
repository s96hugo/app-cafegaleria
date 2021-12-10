package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
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
import com.example.galeria.models.Ticket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageButton setting, ref;
    TextView nombre, email;
    boolean cb1,cb2,cb3,cb4,cm1,cm2,cm3,cm4,cm5,cp1,cp2,cp3,cp4,cp5;
    Button b1,b2,b3,b4,m1,m2,m3,m4, m5,p1,p2,p3,p4,p5;
    Ticket tb1, tb2, tb3, tb4, tm1, tm2, tm3, tm4, tm5, tp1, tp2, tp3, tp4, tp5;
    private SharedPreferences sharedPreferences;
    private List<Ticket> tickets;
    private RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);

        b1 = findViewById(R.id.bb1);
        b2 = findViewById(R.id.bb2);
        b3 = findViewById(R.id.bb3);
        b4 = findViewById(R.id.bb4);
        m1 = findViewById(R.id.bm1);
        m2 = findViewById(R.id.bm2);
        m3 = findViewById(R.id.bm3);
        m4 = findViewById(R.id.bm4);
        m5 = findViewById(R.id.bm5);
        p1 = findViewById(R.id.bp1);
        p2 = findViewById(R.id.bp2);
        p3 = findViewById(R.id.bp3);
        p4 = findViewById(R.id.bp4);
        p5 = findViewById(R.id.bp5);

        nombre = findViewById(R.id.tvNombre);
        email = findViewById(R.id.tvEmail);
        setting = findViewById(R.id.buttonSettings);
        ref = findViewById(R.id.ibRefresh);

        tickets = new ArrayList<>();
        nombre.setText(sharedPreferences.getString("name", ""));
        email.setText(sharedPreferences.getString("email", ""));

        setting.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            onPause();
        });

        ref.setOnClickListener(view -> {
            getTickets();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getTickets();
    }

    private void getTickets() {
        tickets.clear();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.GET_CURRENT_TICKETS,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                JSONArray listTick = new JSONArray(response.getString("tickets"));

                                for(int i = 0; i< listTick.length();i++){
                                    JSONObject tick = listTick.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Ticket ticket = gson.fromJson(tick.toString(), Ticket.class);
                                    tickets.add(ticket);
                                }

                                cb1 = true;
                                cb2 = true;
                                cb3 = true;
                                cb4 = true;
                                cm1 = true;
                                cm2 = true;
                                cm3 = true;
                                cm4 = true;
                                cm5 = true;
                                cp1 = true;
                                cp2 = true;
                                cp3 = true;
                                cp4 = true;
                                cp5 = true;

                                tb1 = null;
                                tb2 = null;
                                tb3 = null;
                                tb4 = null;
                                tm1 = null;
                                tm2 = null;
                                tm3 = null;
                                tm4 = null;
                                tm5 = null;
                                tp1 = null;
                                tp2 = null;
                                tp3 = null;
                                tp4 = null;
                                tp5 = null;

                                b1.setBackgroundColor(getResources().getColor(R.color.green));
                                b2.setBackgroundColor(getResources().getColor(R.color.green));
                                b3.setBackgroundColor(getResources().getColor(R.color.green));
                                b4.setBackgroundColor(getResources().getColor(R.color.green));
                                m1.setBackgroundColor(getResources().getColor(R.color.green));
                                m2.setBackgroundColor(getResources().getColor(R.color.green));
                                m3.setBackgroundColor(getResources().getColor(R.color.green));
                                m4.setBackgroundColor(getResources().getColor(R.color.green));
                                m5.setBackgroundColor(getResources().getColor(R.color.green));
                                p1.setBackgroundColor(getResources().getColor(R.color.green));
                                p2.setBackgroundColor(getResources().getColor(R.color.green));
                                p3.setBackgroundColor(getResources().getColor(R.color.green));
                                p4.setBackgroundColor(getResources().getColor(R.color.green));
                                p5.setBackgroundColor(getResources().getColor(R.color.green));

                                for(Ticket t : tickets){
                                    if(t.getTable_id()==1){
                                        b1.setBackgroundColor(getResources().getColor(R.color.red));
                                        cb1 = false;
                                        tb1 = t;
                                    }else if (t.getTable_id() == 2){
                                        b2.setBackgroundColor(getResources().getColor(R.color.red));
                                        cb2 = false;
                                        tb2 = t;
                                    }else if (t.getTable_id() == 3){
                                        b3.setBackgroundColor(getResources().getColor(R.color.red));
                                        cb3 = false;
                                        tb3 = t;
                                    }else if (t.getTable_id() == 4){
                                        b4.setBackgroundColor(getResources().getColor(R.color.red));
                                        cb4 = false;
                                        tb4 = t;
                                    }else if (t.getTable_id() == 5){
                                        m1.setBackgroundColor(getResources().getColor(R.color.red));
                                        cm1 = false;
                                        tm1 = t;
                                    }else if (t.getTable_id() == 6){
                                        m2.setBackgroundColor(getResources().getColor(R.color.red));
                                        cm2 = false;
                                        tm2 = t;
                                    }else if (t.getTable_id() == 7){
                                        m3.setBackgroundColor(getResources().getColor(R.color.red));
                                        cm3 = false;
                                        tm3 = t;
                                    }else if (t.getTable_id() == 8){
                                        m4.setBackgroundColor(getResources().getColor(R.color.red));
                                        cm4 = false;
                                        tm4 = t;
                                    }else if (t.getTable_id() == 9){
                                        m5.setBackgroundColor(getResources().getColor(R.color.red));
                                        cm5 = false;
                                        tm5 = t;
                                    } else if (t.getTable_id() == 10){
                                        p1.setBackgroundColor(getResources().getColor(R.color.red));
                                        cp1 = false;
                                        tp1 = t;
                                    }else if (t.getTable_id() == 11){
                                        p2.setBackgroundColor(getResources().getColor(R.color.red));
                                        cp2 = false;
                                        tp2 = t;
                                    }else if (t.getTable_id() == 12){
                                        p3.setBackgroundColor(getResources().getColor(R.color.red));
                                        cp3 = false;
                                        tp3 = t;
                                    }else if (t.getTable_id() == 13){
                                        p4.setBackgroundColor(getResources().getColor(R.color.red));
                                        cp4 = false;
                                        tp4 = t;
                                    }else if (t.getTable_id() == 14){
                                        p5.setBackgroundColor(getResources().getColor(R.color.red));
                                        cp5 = false;
                                        tp5 = t;
                                    }else {
                                    }
                                }

                                b1.setOnClickListener(view -> {
                                    if(cb1){
                                        showCreateTicketDialog(1, "barra 1");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tb1);
                                        intent.putExtra("pedido", "barra 1");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                b2.setOnClickListener(view -> {
                                    if(cb2){
                                        showCreateTicketDialog(2, "barra 2");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tb2);
                                        intent.putExtra("pedido", "barra 2");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                b3.setOnClickListener(view -> {
                                    if(cb3){
                                        showCreateTicketDialog(3, "barra 3");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tb3);
                                        intent.putExtra("pedido", "barra 3");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                b4.setOnClickListener(view -> {
                                    if(cb4){
                                        showCreateTicketDialog(4, "barra 4");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tb4);
                                        intent.putExtra("pedido", "barra 4");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                m1.setOnClickListener(view -> {
                                    if(cm1){
                                        showCreateTicketDialog(5, "mesa 1");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tm1);
                                        intent.putExtra("pedido", "mesa 1");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                m2.setOnClickListener(view -> {
                                    if(cm2){
                                        showCreateTicketDialog(6, "mesa 2");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tm2);
                                        intent.putExtra("pedido", "mesa 2");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                m3.setOnClickListener(view -> {
                                    if(cm3){
                                        showCreateTicketDialog(7, "mesa 3");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tm3);
                                        intent.putExtra("pedido", "mesa 3");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                m4.setOnClickListener(view -> {
                                    if(cm4){
                                        showCreateTicketDialog(8, "mesa 4");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tm4);
                                        intent.putExtra("pedido", "mesa 4");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                m5.setOnClickListener(view -> {
                                    if(cm5){
                                        showCreateTicketDialog(9, "mesa 5");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tm5);
                                        intent.putExtra("pedido", "mesa 5");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                p1.setOnClickListener(view -> {
                                    if(cp1){
                                        showCreateTicketDialog(10, "pasillo 1");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tp1);
                                        intent.putExtra("pedido", "pasillo 1");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                p2.setOnClickListener(view -> {
                                    if(cp2){
                                        showCreateTicketDialog(11, "pasillo 2");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tp2);
                                        intent.putExtra("pedido", "pasillo 2");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                p3.setOnClickListener(view -> {
                                    if(cp3){
                                        showCreateTicketDialog(12, "pasillo 3");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tp3);
                                        intent.putExtra("pedido", "pasillo 3");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                p4.setOnClickListener(view -> {
                                    if(cp4){
                                        showCreateTicketDialog(13, "pasillo 4");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tp4);
                                        intent.putExtra("pedido", "pasillo 4");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });

                                p5.setOnClickListener(view -> {
                                    if(cp5){
                                        showCreateTicketDialog(14, "pasillo 5");
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("ticket", tp5);
                                        intent.putExtra("pedido", "pasillo 5");
                                        startActivity(intent);
                                        //onPause();
                                    }
                                });


                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
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

    public void showCreateTicketDialog(int id, String mesa){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ticket_dialog);

        Button bticket = dialog.findViewById(R.id.bCreateTicket);
        TextView tvmesa = dialog.findViewById(R.id.tvMesa);
        tvmesa.setText("en " + mesa);
        dialog.show();

        bticket.setOnClickListener(view -> {
            createTicket(id, mesa);
            dialog.dismiss();
        });

    }

    public void createTicket(int table_id, String mesa){
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("table_id", String.valueOf(table_id));

        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.CREATE_TICKET,
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject ticketJS = response.getJSONObject("ticket");
                                Gson gson = new Gson();
                                Ticket ticket = gson.fromJson(ticketJS.toString(), Ticket.class);
                                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                intent.putExtra("ticket", ticket);
                                intent.putExtra("pedido", mesa);
                                startActivity(intent);
                                //onPause();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Ya existe un ticket en esta mesa", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            String token = sharedPreferences.getString("token", "");
            HashMap<String, String> map = new HashMap<>();
            map.put("Authorization", "Bearer " + token);
            return map;
        }};
        rq.add(req);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}