package com.example.galeria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<String> settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
                case 0: Toast.makeText(SettingsActivity.this, "Has pulsado", Toast.LENGTH_SHORT).show();
                        break;
                case 1: Toast.makeText(SettingsActivity.this, "Has pulsado", Toast.LENGTH_SHORT).show();
                    break;
                case 2: Toast.makeText(SettingsActivity.this, "Has pulsado", Toast.LENGTH_SHORT).show();
                    break;
                case 3: Toast.makeText(SettingsActivity.this, "Has pulsado", Toast.LENGTH_SHORT).show();
                    break;
                case 4: Toast.makeText(SettingsActivity.this, "Has pulsado", Toast.LENGTH_SHORT).show();
                    break;
                default: Toast.makeText(SettingsActivity.this, "Seleccione una acción", Toast.LENGTH_SHORT).show();
                        break;
            }


                });
    }

}