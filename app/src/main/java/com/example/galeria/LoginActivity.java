package com.example.galeria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    Button iniciar;
    EditText email, password;
    RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rq = Volley.newRequestQueue(this);

        email = findViewById(R.id.eTEmail);
        password = findViewById(R.id.eTPassword);
        iniciar = findViewById(R.id.BIniciar);

        iniciar.setOnClickListener(view -> {
            if(validate()){
                login();
            }
        });

    }

    private boolean validate(){
        CharSequence text;
        if(email.getText().toString().isEmpty()){
            text = "Inserte email";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if(password.getText().toString().isEmpty()){
            text = "Inserte contraseña";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    private void login(){
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("email", email.getText().toString().trim().toLowerCase());
        datos.put("password", password.getText().toString().trim());

        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.LOGIN,
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                JSONObject user = response.getJSONObject("user");

                                //clave valor
                                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPref.edit();
                                editor.putString("token", response.getString("token"));
                                editor.putString("id", user.getString("id"));
                                editor.putString("name", user.getString("name"));
                                editor.putString("email", user.getString("email"));
                                editor.apply();
                                //Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
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
        });
        rq.add(req);
    }

}