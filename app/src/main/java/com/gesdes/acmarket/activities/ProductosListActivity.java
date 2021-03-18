package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.adapters.CategoriasAdapter;
import com.gesdes.acmarket.adapters.ProductosAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.PedidoDetalleDao;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductosListActivity extends AppCompatActivity implements TextWatcher {

    int REQUEST_ACTIVITY_RESULT=1;
    ProgressBar progressBar;
    GridView gridView;
    ProductosAdapter productosAdapter;
    List<ProductosModel> listaProductos,listaProductos2;
    String PK_TIENDA,URL_API;
    PedidoModel pedido;
    AppDatabase db;
    TextView tvNombre,tvTitleProductos,tvContadorProductos;
    String CATEGORIA;
    EditText etbuscaProductos;
    TiendasModel TIENDA_OBJ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_list);

        progressBar=findViewById(R.id.pbCargandoProductosList);
        tvContadorProductos=findViewById(R.id.tcContadorCarritoProductosList);
        etbuscaProductos=findViewById(R.id.etBuscarProductosProductosList);
        etbuscaProductos.addTextChangedListener(this);

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        pedido = (PedidoModel) bolsa.getSerializable("pedido");
        TIENDA_OBJ = (TiendasModel) bolsa.getSerializable("TIENDA_OBJ");
        CATEGORIA =  pedido.CLASIFICACION;

        URL_API=getString(R.string.URL_HOST)+"ProductosByTiendaAndCategoria";

        listaProductos=new ArrayList<>();
        listaProductos2=new ArrayList<>();

        gridView = findViewById(R.id.listaProductosProductosList);
        productosAdapter = new ProductosAdapter(this, listaProductos);
        gridView.setAdapter(productosAdapter);

        gridView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductosModel producto=listaProductos.get(position);

                Bundle bolsa=new Bundle();
                bolsa.putSerializable("producto", producto);
                bolsa.putSerializable("TIENDA_OBJ", TIENDA_OBJ);
                Intent intento=new Intent(getApplicationContext(), AgregarProductoActivity.class);
                intento.putExtra("bolsa",bolsa);
                startActivity(intento);
            }
        });
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        obtenerProductosPortiendayCategoria();

        tvNombre=findViewById(R.id.tvNombreProductosCategoriasList);
        tvTitleProductos=findViewById(R.id.tvTitleProductosProductosList);
        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);

        tvTitleProductos.setText(pedido.TIENDA+" - "+CATEGORIA);

    }

    public void obtenerProductosPortiendayCategoria(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_TIENDA", pedido.PK_TIENDA);
            datos.put("PK_CATEGORIA", pedido.PK_CATEGORIA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressBar!=null){
                                progressBar.setVisibility(View.GONE);
                            }

                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONArray categorias=response.getJSONArray("productos");
                                ProductosModel aux;
                                if(listaProductos==null){
                                    listaProductos=new ArrayList<>();
                                }
                                for (int i=0;i<categorias.length();i++) {
                                    JSONObject categoria= categorias.getJSONObject(i);
                                    aux=new ProductosModel();
                                    aux.PK=categoria.getString("pk");
                                    aux.PK_TIENDA=categoria.getString("pk");
                                    aux.PRODUCTO=categoria.getString("producto");
                                    aux.IMAGEN=categoria.getString("imagen");
                                    aux.DESCRIPCION=categoria.getString("descripcion");
                                    aux.STOCK=categoria.getInt("stock");
                                    aux.BORRADO=categoria.getString("borrado");
                                    aux.PRECIO=categoria.getDouble("precio");
                                    listaProductos.add(aux);
                                    listaProductos2.add(aux);
                                }
                                productosAdapter.setListaProductos(listaProductos);
                                productosAdapter.notifyDataSetChanged();

                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("Error",error);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(progressBar!=null){
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.e("Rest Response",error.toString());
                    }
                }
        ){
            //here I want to post data to sever
        };

        int MY_SOCKET_TIMEOUT_MS = 15000;
        int maxRetries = 2;
        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        requstQueue.add(jsonObjectRequest);

    }


    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==REQUEST_ACTIVITY_RESULT && resultCode==RESULT_OK)
        {
            Bundle bolsa=data.getBundleExtra("bolsa");
            PedidoDetalleModel detalle=(PedidoDetalleModel)bolsa.getSerializable("");
            if(pedido.LISTA==null){pedido.LISTA= new ArrayList<>();}
            pedido.LISTA.add(detalle);

        }
    }*/

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onStart() {
        super.onStart();
            List<PedidoDetalleModel> pedidos = db.PedidoDetalleDao().getAll();
            //Toast.makeText(this, "Cantidad " + pedidos.size(), Toast.LENGTH_LONG).show();
            tvContadorProductos.setText(""+pedidos.size());

    }

    public void AbrirCarrito(View view){
        Bundle bolsa= new Bundle();
        bolsa.putSerializable("TIENDA_OBJ",TIENDA_OBJ);
        Intent intento=new Intent(this, com.gesdes.acmarket.activities.MiPedidoActivity.class);
        intento.putExtra("bolsa",bolsa);
        startActivity(intento);
    };

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String s=charSequence.toString().toLowerCase();
        listaProductos.clear();
        for(ProductosModel producto : listaProductos2){
            if(producto.PRODUCTO.toLowerCase().contains(s)){
                listaProductos.add(producto);
            }
        }
        productosAdapter.notifyDataSetChanged();

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}
