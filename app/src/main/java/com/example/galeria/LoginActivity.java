package com.example.galeria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.services.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        email = findViewById(R.id.eTEmail);
        password = findViewById(R.id.eTPassword);
        Button iniciar = findViewById(R.id.BIniciar);

        iniciar.setOnClickListener(view -> {
            if(validate()){
                login();
            }
        });
    }

    /**
     * Valida si el email es correcto o incorrecto. Se considera incorrecto a enviar
     * alguno de los campos en blanco. Muestra un toast con el mensaje de error.
     * @return true si es correcto, false en caso contrario
     */
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

    /**
     * ENDPOINT Login, devuelve el token, junto con los datos de usuario si el login
     * es correcto. Guarda ambos datos en un objeto sharedpreferences.
     */
    private void login(){

        Map<String, String> datos = new HashMap<>();
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
                                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPref.edit();
                                editor.putString("token", response.getString("token"));
                                editor.putString("id", user.getString("id"));
                                editor.putString("name", user.getString("name"));
                                editor.putString("email", user.getString("email"));
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
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
        });
        requestQueue.add(req);

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}