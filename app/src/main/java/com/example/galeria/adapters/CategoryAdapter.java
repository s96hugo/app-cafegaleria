package com.example.galeria.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.CategoyActivity;
import com.example.galeria.Constant;
import com.example.galeria.LoginActivity;
import com.example.galeria.MainActivity;
import com.example.galeria.R;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.models.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {
    private List<Category> lista;
    private List<Category> listaFull;
    private OnRefreshViewListener orvl;
    Context context;

    //tipo: 0 CategoryActivity, 1 OrderActivity (para el size del textView)
    public CategoryAdapter(List<Category> lista, Context context){
        this.lista = lista;
        this.listaFull = new ArrayList<>(lista);
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_categories, viewGroup, false);
        CategoryViewHolder cvh = new CategoryViewHolder(categoryView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int i) {
        holder.txCateg.setText(lista.get(i).toString());
        holder.cel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog(lista.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void showOptionsDialog(Category category){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.options_dialog);
        dialog.show();

        Button edit = dialog.findViewById(R.id.bedita);
        Button delete = dialog.findViewById(R.id.belimin);
        TextView cat = dialog.findViewById(R.id.tvCategoryOption);
        cat.setText(category.getCategory());

        edit.setOnClickListener(view -> {
            showEditDialog(category);
            dialog.dismiss();
        });

        delete.setOnClickListener(view -> {
            checkDeleteCategory(category);
            dialog.dismiss();
        });
    }

    public void showEditDialog(Category category){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.category_dialog);
        EditText tx = dialog.findViewById(R.id.etCategory);
        Button addd = dialog.findViewById(R.id.badd);
        tx.setText(category.getCategory());
        dialog.show();

        addd.setOnClickListener(view -> {
            if(tx.getText().toString().isEmpty()){
                Toast.makeText(context, "La categoría no puede estar vacía", Toast.LENGTH_SHORT).show();
            } else{
                editCategory(new Category(category.getId(), tx.getText().toString()));
                dialog.dismiss();
            }

        });
    }

    public void editCategory(Category category){
        RequestQueue rq = Volley.newRequestQueue(context);
        SharedPreferences  sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("category", category.getCategory().substring(0, 1).toUpperCase(Locale.ROOT) + category.getCategory().substring(1));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/categories/" + category.getId()+"/update",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                orvl = (OnRefreshViewListener)context;
                                orvl.refreshView();
                                Toast.makeText(context, "Categoría editada", Toast.LENGTH_SHORT).show();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(context, "Sesión caducada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Login error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(req);
    }

    public void checkDeleteCategory(Category category){
        RequestQueue rq = Volley.newRequestQueue(context);
        SharedPreferences  sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Constant.HOME+"/category/" + category.getId()+"/HasProduct",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                if(response.getBoolean("deleteable")){
                                    deleteCategory(category);
                                } else {
                                    Toast.makeText(context, "Esta categoría tiene productos asociados", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(context, "Sesión caducada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Login error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en check", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(req);
    }

    public void deleteCategory(Category category){
        RequestQueue rq = Volley.newRequestQueue(context);
        SharedPreferences  sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/categories/" + category.getId()+"/delete",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                orvl = (OnRefreshViewListener)context;
                                orvl.refreshView();
                                Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(context, "Sesión caducada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Login error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        rq.add(req);
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    public Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Category> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(listaFull);

            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Category cat : listaFull){
                    if(cat.getCategory().toLowerCase().contains(filterPattern)){
                        filteredList.add(cat);
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


    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txCateg;
        LinearLayout cel;

        public CategoryViewHolder(View categoryView) {
            super(categoryView);
            txCateg = categoryView.findViewById(R.id.idCateName);
            cel = categoryView.findViewById(R.id.idTopLayout);
        }
    }
}
