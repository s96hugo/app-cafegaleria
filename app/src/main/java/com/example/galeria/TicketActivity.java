package com.example.galeria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.TicketClosedAdapter;
import com.example.galeria.models.Category;
import com.example.galeria.models.Table;
import com.example.galeria.models.Ticket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mrv;
    private TicketClosedAdapter tca;

    private SharedPreferences sharedPreferences;
    private RequestQueue rq;
    private List<Ticket> tickets;
    private List<Table> tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srlTicket);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.purple_200);

        mrv = (RecyclerView) findViewById(R.id.idRecyclerViewTicket);
        mrv.setHasFixedSize(true);
        mrv.setLayoutManager(new LinearLayoutManager(this));

        //DataSet
        tickets = new ArrayList<>();
        tables = new ArrayList<>();
        getTickets();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTickets();
                tca.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void getTickets() {
        tickets.clear();
        tables.clear();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.GET_CLOSED_TICKETS,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                JSONArray listTickets = new JSONArray(response.getString("tickets"));

                                for(int i = 0; i< listTickets.length();i++){
                                    JSONObject ti = listTickets.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Ticket ticket = gson.fromJson(ti.toString(),Ticket.class);
                                    tickets.add(ticket);
                                }


                                JSONArray listTables = new JSONArray(response.getString("tables"));

                                for(int i = 0; i< listTables.length();i++){
                                    JSONObject ta = listTables.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Table table = gson.fromJson(ta.toString(), Table.class);
                                    tables.add(table);
                                }

                                tca = new TicketClosedAdapter(tickets, tables, getApplicationContext());
                                mrv.setAdapter(tca);


                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TicketActivity.this, LoginActivity.class);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TicketActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}