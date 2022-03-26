package com.example.galeria.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.galeria.Constant;
import com.example.galeria.R;
import com.example.galeria.comparators.ComparatorProduct;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.interfaces.OpenProductsByCategory;
import com.example.galeria.models.Category;
import com.example.galeria.models.Product;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {
    private List<Product> lista;
    private List<Product> listaFull;
    private List<Category> categorias;
    private OnRefreshViewListener orvl;
    Context context;

    public ProductAdapter(List<Product> lista, Context context, List<Category> categorias){
        this.lista = lista;
        this.listaFull = new ArrayList<>(lista);
        this.context = context;
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_products, viewGroup, false);
        ProductAdapter.ProductViewHolder cvh = new ProductAdapter.ProductViewHolder(categoryView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int i) {
        holder.txCateg.setText(lista.get(i).getCategory());
        holder.txName.setText(lista.get(i).getName());
        holder.txPrice.setText(String.format("%.2f",lista.get(i).getPrice())+"€");
        holder.cel.setOnClickListener(view -> {
            showOptionsDialog(lista.get(i));
        });

    }

    private void showOptionsDialog(Product product) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.options_dialog);
        dialog.show();

        Button edit = dialog.findViewById(R.id.bedita);
        Button delete = dialog.findViewById(R.id.belimin);
        TextView prod = dialog.findViewById(R.id.tvCategoryOption);
        prod.setText(product.getName());

        edit.setOnClickListener(view -> {
            showEditDialog(product);
            dialog.dismiss();
        });

        delete.setOnClickListener(view -> {
            deleteCategory(product);
            dialog.dismiss();
        });
    }

    private void showEditDialog(Product product) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.product_dialog);

        EditText name = dialog.findViewById(R.id.etName);
        EditText price = dialog.findViewById(R.id.etPrice);
        Button addd = dialog.findViewById(R.id.bsave);
        Spinner category = (Spinner)dialog.findViewById(R.id.SpinnerCat);

        ArrayAdapter categoryAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, categorias);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        //Que salga el spinner de editar la categoría previa
        category.setSelection(categorias.indexOf(new Category(product.getCategory_id(), product.getCategory())));
        //category.setSelection(product.getCategory_id()-1);




        name.setText(product.getName());
        price.setText(String.valueOf(product.getPrice()));

        dialog.show();

        addd.setOnClickListener(view -> {
            Category c = (Category)category.getSelectedItem();

            if(name.getText().toString().isEmpty() || price.getText().toString().isEmpty() || category.getSelectedItem()==null){
                Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            } else{
                editProduct(product.getId(), name.getText().toString(), price.getText().toString(), String.valueOf(c.getId()));
                dialog.dismiss();
            }
        });
    }

    private void editProduct(int id, String name, String price, String category) {
        RequestQueue rq = Volley.newRequestQueue(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("name", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1));
        datos.put("price", price);
        datos.put("category_id", category);
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/products/" + id + "/update",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                lista.clear();
                                Gson gson = new Gson();
                                JSONArray  array= new JSONArray(response.getString("products"));
                                for(int i = 0 ; i<array.length(); i++){
                                    JSONObject pr = array.getJSONObject(i);
                                    lista.add(new Product(pr.getInt("id"),
                                            pr.getString("name"),
                                            pr.getString("price"),
                                            pr.getInt("category_id"),
                                            pr.getString("category")) );
                                }

                                orvl = (OnRefreshViewListener)context;
                                orvl.refreshProduct(lista, categorias);
                                Toast.makeText(context, "Producto editado", Toast.LENGTH_SHORT).show();
                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(context, "Sesión caducada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error de conexión o datos incorrectos", Toast.LENGTH_SHORT).show();
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

    private void deleteCategory(Product product){
        RequestQueue rq = Volley.newRequestQueue(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/products/" + product.getId() + "/delete",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                Gson gson = new Gson();
                                JSONObject  array= new JSONObject(response.getString("product"));
                                Product product = gson.fromJson(array.toString(), Product.class);
                                lista.remove(product);
                                orvl = (OnRefreshViewListener)context;
                                orvl.refreshProduct(lista,categorias);
                                Toast.makeText(context, "Producto '" + product.getName().toLowerCase() + "' eliminado", Toast.LENGTH_SHORT).show();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(context, "Sesión caducada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error de conexión o datos incorrectos", Toast.LENGTH_SHORT).show();
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
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    public Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Product> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(listaFull);

            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Product prod : listaFull){
                    if(prod.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(prod);
                    }
                }

                for (Product prod : listaFull){
                    if(prod.getCategory().toLowerCase().contains(filterPattern)){
                        filteredList.add(prod);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            lista.clear();
            lista.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txName, txPrice, txCateg;
        RelativeLayout cel;

        public ProductViewHolder(View categoryView) {
            super(categoryView);
            txName = categoryView.findViewById(R.id.idProductName);
            txPrice = categoryView.findViewById(R.id.idProductPrice);
            txCateg = categoryView.findViewById(R.id.idProductCategory);
            cel = categoryView.findViewById(R.id.idLayoutProd);
        }
    }
}
