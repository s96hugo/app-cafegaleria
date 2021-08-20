package com.example.galeria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button iniciar;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.eTEmail);
        password = findViewById(R.id.eTPassword);
        iniciar = findViewById(R.id.BIniciar);

        iniciar.setOnClickListener(view -> {
            if(validate()){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validate(){
        CharSequence text;
        if(email.getText().toString().isEmpty()){
            text = "inserte email";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if(password.getText().toString().isEmpty()){
            text = "inserte contraseña";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
}