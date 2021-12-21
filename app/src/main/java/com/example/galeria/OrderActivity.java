package com.example.galeria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.galeria.adapters.CategoryAdapter;
import com.example.galeria.adapters.CategoryOrderAdapter;
import com.example.galeria.adapters.MostPopularAdapter;
import com.example.galeria.adapters.OrderedAdapter;
import com.example.galeria.adapters.ProductAdapter;
import com.example.galeria.adapters.ProductByCategoryAdapter;
import com.example.galeria.adapters.UserAdapter;
import com.example.galeria.interfaces.OnRefreshDataOrdered;
import com.example.galeria.interfaces.OnRefreshViewListener;
import com.example.galeria.interfaces.OpenProductsByCategory;
import com.example.galeria.models.Category;
import com.example.galeria.models.Order;
import com.example.galeria.models.Pagado;
import com.example.galeria.models.Product;
import com.example.galeria.models.ProductOrder;
import com.example.galeria.models.Ticket;
import com.example.galeria.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements OnRefreshDataOrdered, OpenProductsByCategory {

    Button bpedir, bcuenta, bticket;
    TextView numerTicket, tipoVisible;
    ImageButton imswitch, imhome;

    SharedPreferences sharedPreferences;

    List<Product> topProd;          //Lista con el top de productos
    List<ProductOrder> pedido;      //Lista con el pedido en la comanda
    List<Category> categories;      //Lista con todas las categorias
    List<Product> products;         //lista con todos los productos
    List<ProductOrder> datosBruto;  //Lista con todos los pedidos del ticket
    RequestQueue rq;

    private RecyclerView mrv;
    private RecyclerView orv;

    private OrderedAdapter oadapt;
    private MostPopularAdapter adapt;
    private CategoryOrderAdapter coa;
    private ProductByCategoryAdapter pbca;

    String mesa;
    Ticket currentTicket;

    boolean visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        rq = Volley.newRequestQueue(this);

        //DataSet
        topProd = new ArrayList<>();
        pedido = new ArrayList<>();
        categories = new ArrayList<>();
        products = new ArrayList<>();
        datosBruto = new ArrayList<>();

        //Instancia rv top productos
        mrv = (RecyclerView)findViewById(R.id.idRecyclerViewTopProd);
        mrv.setHasFixedSize(true);
        mrv.setLayoutManager(new GridLayoutManager(this, 3));


        orv = (RecyclerView) findViewById(R.id.idRecyclerViewOrderedProd);
        orv.setLayoutManager(new LinearLayoutManager(this));


        numerTicket = findViewById(R.id.tvNumeroTicket);
        bcuenta = findViewById(R.id.bCuenta);
        bpedir = findViewById(R.id.bPedir);
        bticket = findViewById(R.id.bTicket);
        imswitch = findViewById(R.id.imSwitch);
        tipoVisible = findViewById(R.id.tvPedir);
        imhome = findViewById(R.id.imHome);
        imhome.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Traer datos del main activity o StateOrderActivity
        mesa = (String) getIntent().getSerializableExtra("pedido");
        currentTicket = (Ticket) getIntent().getSerializableExtra("ticket");
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);


        numerTicket.setText("Comanda " + mesa);

        //Si es 0, se ha abierto del main activity, si se está moviendo por las ventanas de ticket, llamada vale 1 y
        //los datos ya vienen cargados, simplemente los abre el adaptador.
        int llamada = 0;
        try{
            llamada = (int)getIntent().getSerializableExtra("abre");
        } catch (Exception e){
        }
        if(llamada == 1){
            //Rellenar el DS
            topProd = (List<Product>) getIntent().getSerializableExtra("topProducts");
            categories = (List<Category>) getIntent().getSerializableExtra("categories");
            products = (List<Product>) getIntent().getSerializableExtra("products");
            datosBruto = (List<ProductOrder>) getIntent().getSerializableExtra("datosBruto");
            adapt = new MostPopularAdapter(OrderActivity.this,topProd);
            mrv.setAdapter(adapt);

        } else {
            //Rellenar el DS
            getDataSet();//getTopProd(); getCategories(); getProducts();
        }

        //Funcionalidad de los botones
        bcuenta.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, TicketResumeActivity.class);
            intent.putExtra("ticket", currentTicket);
            intent.putExtra("pedido", mesa);
            intent.putExtra("datosBruto", (Serializable) datosBruto);
            intent.putExtra("topProducts", (Serializable) topProd);
            intent.putExtra("products", (Serializable) products);
            intent.putExtra("categories", (Serializable) categories);
            startActivity(intent);
        });

        bpedir.setOnClickListener(view -> {
            if(pedido.isEmpty()){
                Toast.makeText(getApplicationContext(), "Añade productos para hacer un pedido", Toast.LENGTH_SHORT).show();
            } else {
                //createOrder();
                bpedir.setClickable(false);
                //bpedir.setVisibility(View.INVISIBLE);
                createProductOrder();
            }
        });


        imswitch.setOnClickListener(view -> {
            if (visible) {
                visible = false;
            }else{
                visible = true;
            }
            if(visible){
                tipoVisible.setText("Más vendidos");
                adapt = new MostPopularAdapter(OrderActivity.this,topProd);
                mrv.setAdapter(adapt);
                imhome.setVisibility(View.INVISIBLE);

            } else {
                tipoVisible.setText("Todos los productos");
                coa = new CategoryOrderAdapter(categories, products, OrderActivity.this);
                mrv.setAdapter(coa);

            }
        });

        imhome.setOnClickListener(view -> {
            imhome.setVisibility(View.INVISIBLE);
            tipoVisible.setText("Todos los productos");
            coa = new CategoryOrderAdapter(categories, products, OrderActivity.this);
            mrv.setAdapter(coa);

        });

        bticket.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, StateOrderActivity.class);
            intent.putExtra("ticket", currentTicket);
            intent.putExtra("pedido", mesa);
            intent.putExtra("datosBruto", (Serializable) datosBruto);
            intent.putExtra("topProducts", (Serializable) topProd);
            intent.putExtra("products", (Serializable) products);
            intent.putExtra("categories", (Serializable) categories);
            startActivity(intent);
        });
    }



    /**
     * createProductOrder
     * Metodo que crea un pedido.
     * El valor devuelto es una lista json de productOrder
     * con toda la información relativa a ese ticket.
     */
    public void createProductOrder(){

        JSONArray send = new JSONArray();
        Map<String, String> datos = new HashMap<String, String>();

        for(ProductOrder po : pedido){
            datos.clear();
            datos.put("product_id", String.valueOf(po.getProduct_id()));
            datos.put("units", String.valueOf(po.getUnits()));
            datos.put("comment", po.getComment());
            //datos.put("order_id", String.valueOf(order.getId()));

            datos.put("ticket_id", String.valueOf(currentTicket.getId()));
            datos.put("user_id", String.valueOf(sharedPreferences.getString("id", "")));

            JSONObject datoJS = new JSONObject(datos);
            send.put(datoJS);
        }

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                Constant.HOME+"/productOrders/create",
                send,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject success = response.getJSONObject(0);
                            if (success.getBoolean("success")){

                                pedido.clear();
                                datosBruto.clear();
                                Gson gson = new Gson();
                                JSONObject productOrders = response.getJSONObject(1);
                                JSONArray jsonArray = productOrders.getJSONArray("ticketOrderInfo");
                                for(int i = 0; i< jsonArray.length();i++){
                                    JSONObject prod = jsonArray.getJSONObject(i);
                                    ProductOrder po = gson.fromJson(prod.toString(), ProductOrder.class);
                                    datosBruto.add(po);

                                }

                                bpedir.setClickable(true);
                                Intent intent = new Intent(OrderActivity.this, StateOrderActivity.class);
                                intent.putExtra("ticket", currentTicket);
                                intent.putExtra("pedido", mesa);
                                intent.putExtra("datosBruto", (Serializable) datosBruto);
                                intent.putExtra("topProducts", (Serializable) topProd);
                                intent.putExtra("products", (Serializable) products);
                                intent.putExtra("categories", (Serializable) categories);
                                startActivity(intent);

                            } else {

                                JSONObject token = response.getJSONObject(1);
                                if(!token.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Este ticket tiene la cuenta realizada", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
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

    //DataSet
    /**
     * getDataSet
     * Trae toda la información de topProducts, productos, categorias, informacion del
     * ticket(pagado), y la lista con todos los pedidos y las inicializa en sendas listas.
     *
     */
    private void getDataSet(){
        topProd.clear();
        categories.clear();
        products.clear();
        datosBruto.clear();

        JsonObjectRequest requ = new JsonObjectRequest(Request.Method.GET,
                Constant.HOME+"/products/" + currentTicket.getId() + "/dataSet",//DATA_SET,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                Gson gson = new Gson();
                                //Cargar MostPopular
                                JSONArray ListProd = new JSONArray(response.getString("mostPopular"));
                                for(int i = 0; i< ListProd.length();i++){
                                    JSONObject prod = ListProd.getJSONObject(i);
                                    Product product = gson.fromJson(prod.toString(), Product.class);
                                    topProd.add(product);
                                }

                                //CargarCategorias
                                JSONArray listCat = new JSONArray(response.getString("categories"));
                                for(int i = 0; i< listCat.length();i++) {
                                    JSONObject cat = listCat.getJSONObject(i);
                                    Category category = gson.fromJson(cat.toString(), Category.class);
                                    categories.add(category);
                                }

                                //Productos
                                JSONArray  array= new JSONArray(response.getString("products"));
                                for(int i = 0 ; i<array.length(); i++){
                                    JSONObject pr = array.getJSONObject(i);
                                    products.add(new Product(pr.getInt("id"),
                                            pr.getString("name"),
                                            pr.getString("price"),
                                            pr.getInt("category_id"),
                                            pr.getString("category")) );
                                }

                                //ProductOrder - State
                                JSONArray prods = response.getJSONArray("ticketOrderInfo");
                                for(int i = 0; i< prods.length();i++) {
                                    JSONObject prod = prods.getJSONObject(i);
                                    ProductOrder productOrder = gson.fromJson(prod.toString(), ProductOrder.class);
                                    datosBruto.add(productOrder);
                                }

                                adapt = new MostPopularAdapter(OrderActivity.this,topProd);
                                mrv.setAdapter(adapt);

                            } else {

                                if(!response.getBoolean("token")){
                                    Toast.makeText(getApplicationContext(), "Sesión caducada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
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
        rq.add(requ);
    }


    //Métodos de las interfaces para refrescar los datos de mi lista de productos pedidos
    @Override
    public void refreshData(int product_id, int units, String comment, String name) {

        ProductOrder productOrder = new ProductOrder(0,
                units,
                comment,
                name,
                product_id,
                0);

        if(pedido.contains(productOrder)){
            int total = productOrder.getUnits() + pedido.get(pedido.indexOf(productOrder)).getUnits();
            pedido.get(pedido.indexOf(productOrder)).setUnits(total);
        }else{
            pedido.add(0,productOrder);
        }
        oadapt = new OrderedAdapter(OrderActivity.this, pedido);
        orv.setAdapter(oadapt);
    }

    @Override
    public void refreshCurrent(int product_id, int units, String comment, String name) {

        ProductOrder productOrder = new ProductOrder(0,
                units,
                comment,
                name,
                product_id,
                0);

        if(pedido.contains(productOrder) && productOrder.getComment().isEmpty()){
            pedido.get(pedido.indexOf(productOrder)).setComment("");
        }else if(pedido.contains(productOrder) && !productOrder.getComment().isEmpty()){
            pedido.get(pedido.indexOf(productOrder)).setComment(productOrder.getComment());

        }else{
            pedido.add(0,productOrder);
        }

        oadapt = new OrderedAdapter(OrderActivity.this, pedido);
        orv.setAdapter(oadapt);

    }


    /**
     * deleteProductOrdered
     * @param position int
     *                 elimina de la lista pedido de la comanda el elemento en posición
     *                 y actualiza el adaptador del rv con los nuevos datos.
     *       Este método se llama desde OrderedAdapter
     */
    @Override
    public void deleteProductOrdered(int position) {
        pedido.remove(position);
        oadapt = new OrderedAdapter(OrderActivity.this, pedido);
        orv.setAdapter(oadapt);
    }


    /**
     * ProductsByCategory
     * @param products lista
     *                      Metodo que actualiza el recyclerview principal del activity y muestra
     *                 los productos pertenecientes a la categoria seleccionada. También se actualiza
     *                 el text view que muestra en la categoría que estás
     */
    @Override
    public void productsByCategory(List<Product> products) {
        if(products.isEmpty()){
            tipoVisible.setText("Esta categoría no tiene productos");
        } else {
            tipoVisible.setText("Todos los productos > " + products.get(0).getCategory());
        }

        imhome.setVisibility(View.VISIBLE);
        pbca = new ProductByCategoryAdapter(products, OrderActivity.this);
        mrv.setAdapter(pbca);

    }


    /**
     * onBackPressed
     * Si está visible el botón de atrás (para volver a las categorías) te vuelve a las
     * categorías y actualiza el textView a todos los productos, si no te dirige al main activity
     */
    @Override
    public void onBackPressed() {
        if(imhome.getVisibility() == View.VISIBLE){
            imhome.setVisibility(View.INVISIBLE);
            tipoVisible.setText("Todos los productos");
            coa = new CategoryOrderAdapter(categories, products, OrderActivity.this);
            mrv.setAdapter(coa);

        } else {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}