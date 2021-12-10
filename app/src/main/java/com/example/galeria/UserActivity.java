package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.CategoryAdapter;
import com.example.galeria.adapters.UserAdapter;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.models.Category;
import com.example.galeria.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity implements OnRefreshViewListener {

    ImageButton add_;
    RequestQueue rq;
    private List<User> users;
    private SharedPreferences sharedPreferences;
    private RecyclerView mrv;
    private UserAdapter adapt;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srlUser);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.purple_200);

        mrv = (RecyclerView) findViewById(R.id.idRecyclerViewUser);
        mrv.setLayoutManager(new LinearLayoutManager(this));
        
        //DataSet
        users = new ArrayList<>();
        getUsers();

        add_ = findViewById(R.id.imAddUser);
        add_.setOnClickListener(view -> {
            showCreateUserDialog();

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                adapt.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getUsers() {

        users.clear();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.GET_USERS,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                JSONArray listCat = new JSONArray(response.getString("users"));

                                for(int i = 0; i< listCat.length();i++){
                                    JSONObject cat = listCat.getJSONObject(i);
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(cat.toString(), User.class);
                                    users.add(user);
                                }
                                adapt = new UserAdapter(users,UserActivity.this);
                                mrv.setAdapter(adapt);

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_SHORT).show();
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

    private void showCreateUserDialog() {
        final Dialog dialog = new Dialog(UserActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.user_dialog);

        EditText name = dialog.findViewById(R.id.etNameRegistro);
        EditText email = dialog.findViewById(R.id.etEmailRegistro);
        EditText password = dialog.findViewById(R.id.etPasswordRegistro);
        Button addd = dialog.findViewById(R.id.bRegistro);

        dialog.show();

        addd.setOnClickListener(view -> {
            if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();

            } else if(password.getText().toString().length()<8) {
                Toast.makeText(getApplicationContext(), "Contraseña demasiado corta", Toast.LENGTH_SHORT).show();
            } else {
                String nombreSM [] = name.getText().toString().split(" ");
                for(int i = 0 ; i<nombreSM.length ;i++){
                    nombreSM[i] = nombreSM[i].substring(0,1).toUpperCase() + nombreSM[i].substring(1);
                }
                register(unificarNombre(nombreSM), email.getText().toString(), password.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private String unificarNombre(String[] nombreSM) {
        String returnValue = "";
        for (String s: nombreSM){
            returnValue += s + " ";
        }
        return  returnValue.substring(0, returnValue.length()-1);
    }

    private void register(String name, String email, String password) {
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("name", name);
        datos.put("email", email.toLowerCase());
        datos.put("password", password);

        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.REGISTER,
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                                getUsers();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "El usuario debe ser único", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "El usuario debe ser único", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        rq.add(req);
    }

    @Override
    public void refreshView() {
        getUsers();
    }
}