package com.example.galeria.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.example.galeria.Constant;
import com.example.galeria.R;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.models.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> lista;
        private OnRefreshViewListener orvl;
        Context context;

        public UserAdapter(List<User> lista, Context context){
            this.lista = lista;
            this.context = context;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_user, viewGroup, false);
            UserViewHolder uvh = new UserViewHolder(categoryView);
            return uvh;
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int i) {
            holder.txCateg.setText(lista.get(i).getName());
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

        public void showOptionsDialog(User user){

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.options_dialog);
            dialog.show();

            Button edit = dialog.findViewById(R.id.bedita);
            Button delete = dialog.findViewById(R.id.belimin);
            TextView nam = dialog.findViewById(R.id.tvCategoryOption);
            nam.setText(user.getName());

            edit.setOnClickListener(view -> {
                showEditDialog(user);
                dialog.dismiss();
            });

            delete.setOnClickListener(view -> {
                deleteUser(user);
                dialog.dismiss();
            });
        }

    private void deleteUser(User user) {
        RequestQueue rq = Volley.newRequestQueue(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("token", sharedPreferences.getString("token", ""));
        JSONObject datosJs = new JSONObject(datos);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Constant.HOME+"/user/" + user.getId() + "/edit",
                datosJs,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject jsonObject = new JSONObject(response.getString("user"));
                                Gson gson = new Gson();
                                User user1 = gson.fromJson(jsonObject.toString(), User.class);
                                lista.remove(user1);
                                orvl = (OnRefreshViewListener)context;
                                orvl.refreshUsers(lista);
                                Toast.makeText(context, "Usuario '" + user.getName() + "' eliminado", Toast.LENGTH_SHORT).show();

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(context, "Sesión caducada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Permiso denegado: El usuario '" + user.getName() + "' no pudo ser borrado", Toast.LENGTH_LONG).show();
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
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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

    public void showEditDialog(User user){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.user_dialog);
            dialog.show();

            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            TextView tv = dialog.findViewById(R.id.textView);
            EditText name = dialog.findViewById(R.id.etNameRegistro);
            EditText email = dialog.findViewById(R.id.etEmailRegistro);
            EditText pass = dialog.findViewById(R.id.etPasswordRegistro);
            Button edit = dialog.findViewById(R.id.bRegistro);

            tv.setText("Editar usuario");
            name.setText(user.getName());
            email.setText(user.getEmail());

            edit.setOnClickListener(view -> {
                if(name.getText().toString() == null || email.getText().toString() == null || pass.getText().toString() == null){
                    Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else if(pass.length()<8) {
                    Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    userEdit(user.getId(), name.getText().toString(), email.getText().toString(), pass.getText().toString());
                    dialog.dismiss();
                }
            });

        }

        public void userEdit(int id,String name, String email, String password){
            RequestQueue rq = Volley.newRequestQueue(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            Map<String, String> datos = new HashMap<String, String>();
            datos.put("name", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1));
            datos.put("email", email);
            datos.put("password", password);
            JSONObject datosJs = new JSONObject(datos);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Constant.HOME+"/user/" + id + "/edit",
                    datosJs,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    JSONObject jsonObject = new JSONObject(response.getString("user"));
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(jsonObject.toString(), User.class);
                                    lista.remove(user);
                                    lista.add(user);
                                    orvl = (OnRefreshViewListener)context;
                                    orvl.refreshUsers(lista);
                                    Toast.makeText(context, "Usuario '" + user.getName() + "' editado", Toast.LENGTH_SHORT).show();

                                } else {

                                    if(!response.getBoolean("token")){
                                        Toast.makeText(context, "Sesión caducada.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Permiso denegado: No puedes editar la información de un usuario ajeno", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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


        public class UserViewHolder extends RecyclerView.ViewHolder {
            TextView txCateg;
            LinearLayout cel;

            public UserViewHolder(View categoryView) {
                super(categoryView);
                txCateg = categoryView.findViewById(R.id.idUserName);
                cel = categoryView.findViewById(R.id.idTopLayoutUser);
            }
        }
    }

