package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.CategoryAdapter;
import com.example.galeria.adapters.ProductAdapter;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.interfaces.OpenProductsByCategory;
import com.example.galeria.models.Category;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.example.galeria.models.Product;
import com.example.galeria.models.Ticket;
import com.example.galeria.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

public class ProductActivity extends AppCompatActivity implements OnRefreshViewListener {

    ImageButton add_;
    RequestQueue rqc, rq;
    List<Category> categorias;
    List<Product> products;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mrv;
    private ProductAdapter adapt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rqc = Volley.newRequestQueue(this);
        rq = Volley.newRequestQueue(this);

        //DataSet
        categorias = new ArrayList<>();
        products = new ArrayList<>();
        getProducts();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srlProduct);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.purple_200);

        mrv = (RecyclerView) findViewById(R.id.idRecyclerViewProd);
        mrv.setHasFixedSize(true);
        mrv.setLayoutManager(new LinearLayoutManager(this));

        add_ = findViewById(R.id.imCreateProd);

        add_.setOnClickListener(view -> {
            showCreateProductDialog();
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts();
            }
        });
    }

    private void showCreateProductDialog() {
        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.product_dialog);

        EditText name = dialog.findViewById(R.id.etName);
        EditText price = dialog.findViewById(R.id.etPrice);
        Button addd = dialog.findViewById(R.id.bsave);
        Spinner category = (Spinner)dialog.findViewById(R.id.SpinnerCat);

        ArrayAdapter categoryAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        dialog.show();


        addd.setOnClickListener(view -> {
            Category c = (Category)category.getSelectedItem();

            if (name.getText().toString().isEmpty() || price.getText().toString().isEmpty() || category.getSelectedItem()==null) {
                Toast.makeText(getApplicationContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                createProduct(name.getText().toString(), price.getText().toString(), String.valueOf(c.getId()));
                dialog.dismiss();
            }
        });
    }

    private void createProduct(String name, String price, String category) {
        products.clear();
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("name", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1));
        datos.put("price", price);
        datos.put("category_id", category);
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.CREATE_PRODUCT,
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray  array= new JSONArray(response.getString("products"));
                                for(int i = 0 ; i<array.length(); i++){
                                    JSONObject pr = array.getJSONObject(i);
                                    products.add(new Product(pr.getInt("id"),
                                            pr.getString("name"),
                                            pr.getString("price"),
                                            pr.getInt("category_id"),
                                            pr.getString("category")) );
                                }

                                refreshProduct(products,categorias);
                                Toast.makeText(getApplicationContext(), "Producto Añadida", Toast.LENGTH_SHORT).show();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
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

    private void getProducts() {
        categorias.clear();
        products.clear();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.PRODUCTS_AND_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject  res = new JSONObject(response);
                    if(res.getBoolean("success")){
                        JSONArray  array= new JSONArray(res.getString("products"));
                        for(int i = 0 ; i<array.length(); i++){
                            JSONObject pr = array.getJSONObject(i);
                            products.add(new Product(pr.getInt("id"),
                                                    pr.getString("name"),
                                                    pr.getString("price"),
                                                    pr.getInt("category_id"),
                                                    pr.getString("category")) );
                        }

                        JSONArray listCat = new JSONArray(res.getString("categories"));

                        for(int i = 0; i< listCat.length();i++){
                            JSONObject cat = listCat.getJSONObject(i);
                            Gson gson = new Gson();
                            Category category = gson.fromJson(cat.toString(),Category.class);
                            categorias.add(category);
                        }
                        refreshProduct(products,categorias);
                    }
                    else {
                        if(!res.getBoolean("token")){
                            Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
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
                Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(request);
    }

    @Override
    public void refreshView() {
        //Desuso
        getProducts();
    }

    @Override
    public void refreshCategory(List<Category> categories) {
        //Implementado en CategoryActivity
    }

    @Override
    public void refreshProduct(List<Product> products, List<Category> categories) {
        adapt = new ProductAdapter(products,ProductActivity.this, categories);
        mrv.setAdapter(adapt);
        adapt.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshUsers(List<User> users) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapt.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

}