package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.MyExpandableListAdapter;
import com.example.galeria.models.Pagado;
import com.example.galeria.models.ProductOrder;
import com.example.galeria.models.Ticket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateOrderActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    RequestQueue rq;

    Ticket currentTicket;
    String mesa;

    TextView tv;
    Button home;

    List<ProductOrder> datosBruto;
    ArrayList<Integer> groupListt;
    List<String> groupList;
    Map<String, List<ProductOrder>> roundCollection;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_order);

        //Datos del Activity Order
        mesa = (String) getIntent().getSerializableExtra("pedido");
        currentTicket = (Ticket) getIntent().getSerializableExtra("ticket");

        //Sincronizar
        tv = findViewById(R.id.tvStateOrder);
        home = findViewById(R.id.bStateHome);

        tv.setText("Pedido " + mesa);

        home.setOnClickListener(view -> {
            Intent intent = new Intent(StateOrderActivity.this, MainActivity.class);
            startActivity(intent);
        });

        //Instancias
        roundCollection = new HashMap<String, List<ProductOrder>>();
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);


        if((int)getIntent().getSerializableExtra("call") == 1){ //Datos tras hacer pedido
            //DataSet
            datosBruto = new ArrayList<>();
            datosBruto = (List<ProductOrder>) getIntent().getSerializableExtra("datos");
            loadData(datosBruto);


        } else { //Llamada a la api para traer la info
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                    Constant.HOME+"/productOrders/" + currentTicket.getId() + "/info",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    datosBruto = new ArrayList<>();

                                    JSONArray prods = response.getJSONArray("ticketOrderInfo");
                                    for(int i = 0; i< prods.length();i++){
                                        JSONObject prod = prods.getJSONObject(i);
                                        Gson gson = new Gson();
                                        ProductOrder productOrder = gson.fromJson(prod.toString(), ProductOrder.class);
                                        datosBruto.add(productOrder);
                                    }
                                    loadData(datosBruto);

                                } else {

                                    if(!response.getBoolean("token")){
                                        Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(StateOrderActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Aún no se ha realizado ningún pedido en esta mesa.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Error de conexión o datos incorrectos", Toast.LENGTH_SHORT).show();
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
    }

    private void loadData(List<ProductOrder> datosBruto){
        //Sacamos el id orders que hay
        groupListt = new ArrayList<Integer>();
        for (ProductOrder po : datosBruto) {
            if (groupListt.contains(po.getOrder_id())) {
                continue;
            }
            groupListt.add(po.getOrder_id());
        }

        //Creamos una array de listas, y rellenamos cada una con los pedidos que esten en su key (pedido_id)
        List<ProductOrder>[] lista = new List[groupListt.size()];
        int i = 0;
        for (int a : groupListt) {
            lista[i] = new ArrayList<>();
            for (ProductOrder po : datosBruto) {
                if (po.getOrder_id() == a) {
                    lista[i].add(po);
                }
            }
            i++;
        }

        //Insertamos en el mapa cada lista con su key correspondiente
        i = 0;
        for (Integer b : groupListt) {
            roundCollection.put("Ronda " + ((b + i + 1) - b), lista[i]);
            i++;
        }

        i = 1;
        ;
        groupList = new ArrayList<String>();
        for (int po : groupListt) {
            groupList.add("Ronda " + i);
            i++;
        }

        expandableListView = findViewById(R.id.elStateOrder);
        expandableListAdapter = new MyExpandableListAdapter(this, groupList, roundCollection);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.expandGroup(groupList.size() - 1);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;

            @Override
            public void onGroupExpand(int i) {
                if (lastExpandedPosition != -1 && i != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = i;

            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String selected = expandableListAdapter.getChild(i, i1).toString();
                ProductOrder productOrder = (ProductOrder) expandableListAdapter.getChild(i,i1);
                //Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();
                showOptionsDialog(productOrder);
                return true;
            }
        });

    }

    private void showOptionsDialog(ProductOrder productOrder){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.options_dialog);


        Button edit = dialog.findViewById(R.id.bedita);
        Button delete = dialog.findViewById(R.id.belimin);
        TextView cat = dialog.findViewById(R.id.tvCategoryOption);

        edit.setText("Cambiar");
        delete.setText("Quitar");
        cat.setText(productOrder.getName());
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StateOrderActivity.this, OrderActivity.class);
        intent.putExtra("ticket", currentTicket);
        intent.putExtra("pedido", mesa);
        startActivity(intent);
    }
}