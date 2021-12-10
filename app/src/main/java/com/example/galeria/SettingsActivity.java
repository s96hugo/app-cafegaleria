package com.example.galeria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<String> settings;
    private Intent intent;
    RequestQueue rq;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rq = Volley.newRequestQueue(this);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = new ArrayList<String>();
        settings.add("Categorías");
        settings.add("Productos");
        settings.add("Tickets");
        settings.add("Usuarios");
        settings.add("Cerrar sesión");

        lv = findViewById(R.id.lvVId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,settings);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i){
                case 0:
                        intent = new Intent(SettingsActivity.this, CategoyActivity.class);
                        startActivity(intent);
                        break;


                case 1:
                        intent = new Intent(SettingsActivity.this, ProductActivity.class);
                        startActivity(intent);
                        break;


                case 2:
                        intent = new Intent(SettingsActivity.this, TicketActivity.class);
                        startActivity(intent);
                        break;


                case 3:
                        intent = new Intent(SettingsActivity.this, UserActivity.class);
                        startActivity(intent);
                        break;


                case 4:
                        logout();
                        break;


                default:
                        Toast.makeText(SettingsActivity.this, "Seleccione una acción", Toast.LENGTH_SHORT).show();
                        break;
            }
                });
    }

    private void logout() {
        Map<String,String> datos = new HashMap<String, String>();
        datos.put("token", sharedPreferences.getString("token", ""));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.LOGOUT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Sin conexión", Toast.LENGTH_SHORT).show();
            }

            }){
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
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}