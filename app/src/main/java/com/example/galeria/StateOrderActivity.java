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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
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
import com.example.galeria.models.Category;
import com.example.galeria.models.Pagado;
import com.example.galeria.models.Product;
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
import java.util.Locale;
import java.util.Map;

public class StateOrderActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    RequestQueue rq;
    

    List<Category> aux;

    Ticket currentTicket;
    String mesa;

    TextView tv;
    Button home;

    List<Product> products;
    List<Category> categories;
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


        //DataSet
        datosBruto = new ArrayList<>();
        datosBruto = (List<ProductOrder>) getIntent().getSerializableExtra("datosBruto");
        products = new ArrayList<>();
        categories = new ArrayList<>();
        products = (List<Product>) getIntent().getSerializableExtra("products");
        categories = (List<Category>) getIntent().getSerializableExtra("categories");
        aux = new ArrayList<>(categories);
        aux.add(0, new Category(0, "Todas"));

        if(datosBruto.isEmpty()){
            Toast.makeText(getApplicationContext(), "Aun no se ha realizado ningún pedido", Toast.LENGTH_SHORT).show();
        } else {
            loadData(datosBruto);
        }

    }

    private void loadData(List<ProductOrder> datosBruto) {

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
                ProductOrder productOrder = (ProductOrder) expandableListAdapter.getChild(i, i1);
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
        dialog.setContentView(R.layout.state_options_dialog);


        Button edit = dialog.findViewById(R.id.bSt2);
        Button delete = dialog.findViewById(R.id.bSt3);
        Button add = dialog.findViewById(R.id.bSt1);
        TextView producto = dialog.findViewById(R.id.tvSt1);


        producto.setText(productOrder.getName());
        dialog.show();

        edit.setOnClickListener(view -> {
            showEditDialog(productOrder);
            dialog.dismiss();

        });

        delete.setOnClickListener(view -> {
            showDeleteDialog(productOrder);
            dialog.dismiss();
        });

        add.setOnClickListener(view -> {
            showAddDialog(productOrder);
            dialog.dismiss();
        });
    }

    private void showDeleteDialog(ProductOrder productOrder){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.options_dialog);
        dialog.show();

        Button edit = dialog.findViewById(R.id.bedita);
        Button delete = dialog.findViewById(R.id.belimin);
        TextView cat = dialog.findViewById(R.id.tvCategoryOption);

        cat.setTextSize(14);
        cat.setText("Borrar " + productOrder.getName() + " del pedido");

        edit.setText("Cancelar");
        edit.setOnClickListener(view -> {
            dialog.dismiss();
        });

        delete.setOnClickListener(view -> {
            deleteOrder(productOrder);
            dialog.dismiss();
        });
    }

    private void showEditDialog(ProductOrder productOrder) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.state_crud_order);

        TextView titulo = dialog.findViewById(R.id.idStdTvTitulo);
        Spinner categoria = dialog.findViewById(R.id.idStdSpinCat);
        Spinner producto = dialog.findViewById(R.id.idStdSpinProd);
        EditText unidades = dialog.findViewById(R.id.idStdEtUnidades);
        EditText comentario = dialog.findViewById(R.id.idStdEdComentario);
        Button aceptar = dialog.findViewById(R.id.idStdBtGuardar);
        Button cancelar = dialog.findViewById(R.id.idStdBtCancelar);

        titulo.setText(productOrder.getName());
        unidades.setText(String.valueOf(productOrder.getUnits()));
        comentario.setText(productOrder.getComment().equals("") ? "" : productOrder.getComment());

        ArrayAdapter categoryAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, aux);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categoria.setAdapter(categoryAdapter);
        categoria.setSelection(0);


        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Category cat = (Category)categoria.getSelectedItem();
                showProducts(cat, producto, productOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog.show();

        cancelar.setOnClickListener(view -> {
            dialog.dismiss();
        });

        aceptar.setOnClickListener(view -> {
            Product p = (Product)producto.getSelectedItem();
            try{
                if(Integer.parseInt(unidades.getText().toString())>=1 && unidades.getText() != null) {
                    editOrder(productOrder, p, unidades.getText().toString(), comentario.getText().toString());
                    dialog.dismiss();
                }
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Introduce una cantidad y un producto", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showAddDialog(ProductOrder productOrder) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.state_crud_order);

        TextView titulo = dialog.findViewById(R.id.idStdTvTitulo);
        Spinner categoria = dialog.findViewById(R.id.idStdSpinCat);
        Spinner producto = dialog.findViewById(R.id.idStdSpinProd);
        EditText unidades = dialog.findViewById(R.id.idStdEtUnidades);
        EditText comentario = dialog.findViewById(R.id.idStdEdComentario);
        Button aceptar = dialog.findViewById(R.id.idStdBtGuardar);
        Button cancelar = dialog.findViewById(R.id.idStdBtCancelar);

        titulo.setText("Añade un nuevo producto");
        //unidades.setText("1");

        ArrayAdapter categoryAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, aux);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categoria.setAdapter(categoryAdapter);
        categoria.setSelection(0);

        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Category cat = (Category)categoria.getSelectedItem();
                showProducts(cat, producto, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog.show();

        cancelar.setOnClickListener(view -> {
            dialog.dismiss();
        });

        aceptar.setOnClickListener(view -> {
            Product product = (Product) producto.getSelectedItem();
            try{
                if(Integer.parseInt(unidades.getText().toString())>=1 && unidades.getText() != null) {
                    createOrder(productOrder, product, unidades.getText().toString(), comentario.getText().toString());
                    dialog.dismiss();
                }
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Introduce una cantidad y un producto", Toast.LENGTH_SHORT).show();
            }

        });


    }

    /**
     * showProducts
     * @param category categoría es un filtro
     * @param producto Spinner
     * @param productOrder para saber el producto que se pidió
     * Método que carga en el spinner los productos. Si está seleccionada la categoria 'Todas'
     * muestra todos los productos, dejando fijado el producto sobre el que se está editando.
     * Para cada categoria
     */
    public void showProducts(Category category, Spinner producto, ProductOrder productOrder){
        if(category.getCategory().equals("Todas")){
            ArrayAdapter productAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, products);
            productAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            producto.setAdapter(productAdapter);
            producto.setSelection(products.indexOf(productOrder == null ? 0 :new Product(productOrder.getProduct_id(), "", "1.0",1,"")));

        } else {
            List<Product> productsFiltrado = new ArrayList<>();
            for(Product p : products){
                if(p.getCategory_id() == category.getId()){
                    productsFiltrado.add(p);
                }
            }
            ArrayAdapter productAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, productsFiltrado);
            productAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            producto.setAdapter(productAdapter);
        }
    }

    /**
     * delete order
     * @param productOrder
     * elimina un productOrder de ujn order.
     * La logica: hace dos listas con el numero de rondas, si tras eliminar un
     * productOrder detecta que se ha quedado vacia una ronda
     * te saca a la pantalla order Activity para evitar que falle
     * el adaptador.
     */
    public void deleteOrder(ProductOrder productOrder){
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("order_id", String.valueOf(productOrder.getOrder_id()));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/productOrders/" + productOrder.getId() + "/delete",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject jsonArray = response.getJSONObject("productOrder");
                                Gson gson = new Gson();
                                ProductOrder product = gson.fromJson(jsonArray.toString(), ProductOrder.class);

                                List<ProductOrder> aux = new ArrayList<>(datosBruto);

                                List<Integer> check2 = new ArrayList<>();
                                for(ProductOrder po : datosBruto){
                                    if(check2.contains(po.getOrder_id())){

                                    } else {
                                        check2.add(po.getOrder_id());
                                    }
                                }

                                aux.remove(product);
                                List<Integer> check1 = new ArrayList<>();
                                for(ProductOrder po1 : aux){
                                    if(check1.contains(po1.getOrder_id())){

                                    } else {
                                        check1.add(po1.getOrder_id());
                                    }
                                }


                                if(check1.size() == check2.size()){
                                    datosBruto.remove(product);
                                    loadData(datosBruto);
                                } else {
                                    datosBruto.remove(product);
                                    Toast.makeText(getApplicationContext(), "Ronda eliminada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StateOrderActivity.this, OrderActivity.class);
                                    intent.putExtra("ticket", currentTicket);
                                    intent.putExtra("pedido", mesa);
                                    intent.putExtra("datosBruto", (Serializable) datosBruto);
                                    intent.putExtra("topProducts", (Serializable) getIntent().getSerializableExtra("topProducts"));
                                    intent.putExtra("products", (Serializable) getIntent().getSerializableExtra("products"));
                                    intent.putExtra("categories", (Serializable) getIntent().getSerializableExtra("categories"));
                                    intent.putExtra("abre", 1);
                                    startActivity(intent);
                                }

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StateOrderActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "No se puede cambiar un pedido a un ticket al que se le ha realizado la cuenta", Toast.LENGTH_LONG).show();
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

    public void editOrder(ProductOrder productOrder, Product product, String units, String comment){
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("order_id", String.valueOf(productOrder.getOrder_id()));
        datos.put("units", units);
        datos.put("comment", comment);
        datos.put("product_id", String.valueOf(product.getId()));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/productOrders/" + productOrder.getId() + "/update",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                datosBruto.clear();
                                JSONArray jsonArray = response.getJSONArray("ticketOrderInfo");
                                Gson gson = new Gson();
                                for (int i = 0 ; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    ProductOrder product = gson.fromJson(jsonObject.toString(), ProductOrder.class);
                                    datosBruto.add(product);
                                }

                                loadData(datosBruto);

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StateOrderActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "No se puede hacer un pedido a un ticket al que se le ha realizado la cuenta", Toast.LENGTH_LONG).show();
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

    public void createOrder(ProductOrder productOrder, Product product, String units, String comment){
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("order_id", String.valueOf(productOrder.getOrder_id()));
        datos.put("units", units);
        datos.put("comment", comment);
        datos.put("product_id", String.valueOf(product.getId()));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/productOrders/crear",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                datosBruto.clear();
                                JSONArray jsonArray = response.getJSONArray("ticketOrderInfo");
                                Gson gson = new Gson();
                                for (int i = 0 ; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    ProductOrder product = gson.fromJson(jsonObject.toString(), ProductOrder.class);
                                    datosBruto.add(product);
                                }

                                loadData(datosBruto);

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StateOrderActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "No se puede hacer un pedido a un ticket al que se le ha realizado la cuenta", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        //Si es 0, se ha abierto del Order activity, si viene de cambiar en ResumenActivity, llamada vale 1 y
        int llamada = 0;
        try{
            llamada = (int)getIntent().getSerializableExtra("abre");
        } catch (Exception e){
        }
        if(llamada == 1){//Caso 1 -> a TicketResume
            Intent intent = new Intent(StateOrderActivity.this, TicketResumeActivity.class);
            intent.putExtra("ticket", currentTicket);
            intent.putExtra("pedido", mesa);
            intent.putExtra("datosBruto", (Serializable) datosBruto);
            intent.putExtra("topProducts", (Serializable) getIntent().getSerializableExtra("topProducts"));
            intent.putExtra("products", (Serializable) getIntent().getSerializableExtra("products"));
            intent.putExtra("categories", (Serializable) getIntent().getSerializableExtra("categories"));
            startActivity(intent);

        } else {//caso 0 -> a OrderActivity
            Intent intent = new Intent(StateOrderActivity.this, OrderActivity.class);
            intent.putExtra("ticket", currentTicket);
            intent.putExtra("pedido", mesa);
            intent.putExtra("datosBruto", (Serializable) datosBruto);
            intent.putExtra("topProducts", (Serializable) getIntent().getSerializableExtra("topProducts"));
            intent.putExtra("products", (Serializable) getIntent().getSerializableExtra("products"));
            intent.putExtra("categories", (Serializable) getIntent().getSerializableExtra("categories"));
            intent.putExtra("abre", 1);
            startActivity(intent);

        }

    }
}