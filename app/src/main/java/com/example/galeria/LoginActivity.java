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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


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
     * Valida si el campo usuario y contraseña no están vacios
     * @return true si rellenos, false si vacío 1 campo.
     */
    private boolean validate(){
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            CharSequence text = "Complete los campos usuario y contraseña.";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    /**
     * Llama al endpoint login con el usuario y la contraseña establecida como
     * parámetros. Si el login es correcto, se guarda la info del usuario en un objeto
     * sharePreferences y la app se dirige al MainActivity. En caso contrario devuelte el
     * pertinente error: login incorrecto, fallo de conexión etc.
     */
    private void login(){
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("email", email.getText().toString().trim().toLowerCase());
        datos.put("password", password.getText().toString().trim());
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.LOGIN,
                datosJs,
                response -> {
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

                }, error -> Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
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