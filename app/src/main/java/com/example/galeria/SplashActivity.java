package com.example.galeria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    /**
     * Si devuelve -1 el id de shared preferences significa que es la priera vez que se ejecuta
     * la aplicación y te anda al login activity. En caso contrario intenta el login para
     * verificar que el token siga activo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        int id = Integer.parseInt(sharedPreferences.getString("id", "-1"));
        if(id == -1){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            tryLogin(id);
        }
    }

    /**
     * Prueba a traer los datos del usuario que está haciendo la llamada con el único propósito de
     * comprobar que el token sigue activo. Si está activo la app se dirige al main activity, en caso
     * contrario se dirige al login activity.
     * @param id del usuario activo
     */
    private void tryLogin(int id) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.HOME+"/user/" + id + "/get",
                null,
                response -> {
                    try {
                        Intent intent;
                        if (response.getBoolean("success")) {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        } else {
                            Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, error -> {
                    Toast.makeText(getApplicationContext(), "Sesión caducada o error de conexión", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        requestQueue.add(req);
    }
}