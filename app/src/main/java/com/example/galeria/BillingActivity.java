package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
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
import com.example.galeria.models.Product;
import com.example.galeria.models.Table;
import com.example.galeria.models.Ticket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {

    private ImageButton nuevaConsulta;
    private SharedPreferences sharedPreferences;
    private RequestQueue rq;


    private TextView titulo, fecha, total, efectivo, tarjeta;
    private Button volver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        rq = Volley.newRequestQueue(this);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        volver = findViewById(R.id.idbvolverbilling);

        titulo = findViewById(R.id.idtvtitulobilling);
        fecha = findViewById(R.id.idtvfechabilling);
        total = findViewById(R.id.idtvtotalbilling);
        efectivo = findViewById(R.id.idtvefectivobilling);
        tarjeta = findViewById(R.id.idtvtarjetabilling);


        nuevaConsulta = findViewById(R.id.idibfacturacion);
        nuevaConsulta.setOnClickListener(view -> {
            showOptionsDialog();
        });

        volver.setOnClickListener(view -> {
            Intent intent = new Intent(BillingActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        showOptionsDialog();
    }

    public void showOptionsDialog(){

        final Dialog dialog = new Dialog(BillingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.options_dialog);
        dialog.show();

        Button dia = dialog.findViewById(R.id.bedita);
        Button tramo = dialog.findViewById(R.id.belimin);
        TextView cat = dialog.findViewById(R.id.tvCategoryOption);

        cat.setText("Facturación de un...");
        cat.setTextSize(15);
        dia.setText("Día");
        dia.setTextSize(14);
        tramo.setText("Tramo");
        tramo.setTextSize(14);

        dia.setOnClickListener(view -> {
            showDateDialog(1);
            dialog.dismiss();
        });

        tramo.setOnClickListener(view -> {
            showDateDialog(0);
            dialog.dismiss();
        });
    }

    private void showDateDialog(int i) {
        final Dialog dialog = new Dialog(BillingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.date_dialog);
        dialog.show();

        Button button = dialog.findViewById(R.id.idbDateDialog);
        TextView textView = dialog.findViewById(R.id.idtvDateDialog);
        CalendarView calendarView = dialog.findViewById(R.id.idCalDateDialog);

        if(i == 1){
            button.setText("Consultar");
            textView.setText("¿De qué día desea ver la facturación?"); //Dia
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month,
                                                int dayOfMonth) {

                    month = month+1;
                    String formtMonth = String.valueOf(month);
                    if(month<10) formtMonth = "0"+ month;

                    String formtDay = String.valueOf(dayOfMonth);
                    if(dayOfMonth<10) formtDay = "0"+dayOfMonth;


                    String fechaFromForm = year + "-" + formtMonth + "-" + formtDay;
                    String dateUser = formtDay + "/" + formtMonth + "/" + year;

                    button.setOnClickListener(view1 -> {
                        titulo.setText("Facturación día");
                        fecha.setText(dateUser);
                        billingByDate(i, fechaFromForm, null);
                        dialog.dismiss();
                    });

                }
            });

            button.setOnClickListener(view -> {

                long date = calendarView.getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                month = month+1;
                String formtMonth = String.valueOf(month);
                if(month<10) formtMonth = "0"+ month;

                String formtDay = String.valueOf(day);
                if(day<10) formtDay = "0"+day;

                //customize According to Your requirement
                String fechaFromForm = year + "-" + formtMonth + "-" + formtDay;
                String dateUser = formtDay + "/" + formtMonth + "/" + year;



                titulo.setText("Facturación día");
                fecha.setText(dateUser);

                billingByDate(i, fechaFromForm, null);
                dialog.dismiss();
            });


        } else {
            button.setText("Siguiente");
            textView.setText("¿Desde qué día desea ver la facturación?"); //Tramo

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month,
                                                int dayOfMonth) {

                    month = month+1;
                    String formtMonth = String.valueOf(month);
                    if(month<10) formtMonth = "0"+ month;

                    String formtDay = String.valueOf(dayOfMonth);
                    if(dayOfMonth<10) formtDay = "0"+dayOfMonth;


                    String fechaFromForm = year + "-" + formtMonth + "-" + formtDay;
                    String dateUser = formtDay + "/" + formtMonth + "/" + year;

                    button.setOnClickListener(view1 -> {
                        dialog.dismiss();
                        showDateDialog2(i, fechaFromForm, dateUser);
                    });

                }
            });

            button.setOnClickListener(view -> {

                long date = calendarView.getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                month = month+1;
                String formtMonth = String.valueOf(month);
                if(month<10) formtMonth = "0"+ month;

                String formtDay = String.valueOf(day);
                if(day<10) formtDay = "0"+day;

                //customize According to Your requirement
                String fechaFromForm = year + "-" + formtMonth + "-" + formtDay;
                String dateUser = formtDay + "/" + formtMonth + "/" + year;

                dialog.dismiss();
                showDateDialog2(i, fechaFromForm, dateUser);

            });

        }
    }

    private void showDateDialog2(int i, String fechaFrom, String dateUser) {
        final Dialog dialog = new Dialog(BillingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.date_dialog);
        dialog.show();

        Button button = dialog.findViewById(R.id.idbDateDialog);
        TextView textView = dialog.findViewById(R.id.idtvDateDialog);
        CalendarView calendarView = dialog.findViewById(R.id.idCalDateDialog);

        button.setText("Consultar");
        textView.setText("¿ Desde " + dateUser +" hasta qué día?");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

                month = month+1;
                String formtMonth = String.valueOf(month);
                if(month<10) formtMonth = "0"+ month;

                String formtDay = String.valueOf(dayOfMonth);
                if(dayOfMonth<10) formtDay = "0"+dayOfMonth;


                String fechaToForm = year + "-" + formtMonth + "-" + formtDay;
                String dateUserTo = formtDay + "/" + formtMonth + "/" + year;

                button.setOnClickListener(view1 -> {
                    titulo.setText("Facturación por tramo");
                    fecha.setText("de " + dateUser + " a " + dateUserTo);
                    billingByDate(i, fechaFrom, fechaToForm);
                    dialog.dismiss();
                });

            }
        });

        button.setOnClickListener(view -> {

            long date = calendarView.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            month = month+1;
            String formtMonth = String.valueOf(month);
            if(month<10) formtMonth = "0"+ month;

            String formtDay = String.valueOf(day);
            if(day<10) formtDay = "0"+day;

            //customize According to Your requirement
            String fechaToForm = year + "-" + formtMonth + "-" + formtDay;
            String dateUserTo = formtDay + "/" + formtMonth + "/" + year;



            titulo.setText("Facturación por tramo");
            fecha.setText("de " + dateUser + " a " + dateUserTo);

            billingByDate(i, fechaFrom, fechaToForm);
            dialog.dismiss();
        });
    }

    private void billingByDate(int i, String fechaFrom, String fechaTo) {

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("fechaTo", fechaTo);
        datos.put("fechaFrom", fechaFrom);
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_BILLING+"/"+i,
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                String totaal = response.getString("total");
                                String tarjetaa = response.getString("tarjeta");
                                String efectivoo = response.getString("efectivo");
                                total.setText("Total facturado: " + String.format("%.2f",Double.parseDouble(totaal)) + "€");
                                tarjeta.setText("Se pagó " + String.format("%.2f",Double.parseDouble(tarjetaa)) + "€ con tarjeta");
                                efectivo.setText("Se pagó " + String.format("%.2f",Double.parseDouble(efectivoo)) + "€ en efectivo");

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(BillingActivity.this, LoginActivity.class);
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(BillingActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}