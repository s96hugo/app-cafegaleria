package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.CategoryAdapter;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.models.Category;
import com.example.galeria.models.Product;
import com.example.galeria.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CategoyActivity extends AppCompatActivity implements OnRefreshViewListener {

    ImageButton add_;
    RequestQueue rq;
    private SharedPreferences sharedPreferences;
    private RecyclerView mrv;
    private CategoryAdapter adapt;
    private List<Category> list;
    private SwipeRefreshLayout swipeRefreshLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoy);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srlCategory);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.purple_200);

        mrv = (RecyclerView) findViewById(R.id.idRecyclerViewCate);
        mrv.setLayoutManager(new LinearLayoutManager(this));



        //DataSet
        list = new ArrayList<>();
        getCategories();


        add_= findViewById(R.id.ibCreateCat);
        add_.setOnClickListener(view -> {
            showCreateCategoryDialog();
        } );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCategories();
            }
        });

    }

    private void showCreateCategoryDialog(){
        final Dialog dialog = new Dialog(CategoyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.category_dialog);
        dialog.show();

        EditText tx = dialog.findViewById(R.id.etCategory);
        Button addd = dialog.findViewById(R.id.badd);

        addd.setOnClickListener(view -> {
            if(tx.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Introduzca nueva categoría", Toast.LENGTH_SHORT).show();
            } else {
                createCategory(tx.getText().toString());
                dialog.dismiss();
            }

        });
    }

    private void createCategory(String categoria) {
        Map<String, String> datos = new HashMap<String, String>();
        datos.put("category", categoria.substring(0, 1).toUpperCase(Locale.ROOT) + categoria.substring(1));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.CREATE_CATEGORY,
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                JSONObject jsonObject = new JSONObject(response.toString());
                                Gson gson = new Gson();
                                Category category = gson.fromJson(jsonObject.toString(), Category.class);
                                list.add(category);
                                Toast.makeText(getApplicationContext(), "Categoría Añadida", Toast.LENGTH_SHORT).show();
                                getCategories();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CategoyActivity.this, LoginActivity.class);
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
            public Map<String, String> getHeaders() throws AuthFailureError{
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(req);
    }

    private void getCategories() {

        list.clear();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.GET_ALL_CATEGORY,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                JSONArray listCat = new JSONArray(response.getString("categories"));

                                for(int i = 0; i< listCat.length();i++){
                                    JSONObject cat = listCat.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Category category = gson.fromJson(cat.toString(), Category.class);
                                    list.add(category);
                                }
                                refreshCategory(list);

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CategoyActivity.this, LoginActivity.class);
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
    public void refreshView() {
        getCategories();
    }

    @Override
    public void refreshCategory(List<Category> categories) {
        adapt = new CategoryAdapter(categories,CategoyActivity.this);
        mrv.setAdapter(adapt);
        adapt.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshProduct(List<Product> products, List<Category> categories) {

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