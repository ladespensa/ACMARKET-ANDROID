package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.adapters.ProductosAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusquedaProductosActivity extends AppCompatActivity {

    int REQUEST_ACTIVITY_RESULT=1;
    ProgressBar progressBar;
    GridView gridView;
    ProductosAdapter productosAdapter;
    List<ProductosModel> listaProductos;
    String PK_TIENDA,URL_API;
    AppDatabase db;
    TextView tvContadorProductos;
    EditText etbuscaProductos;
    TiendasModel TIENDA_OBJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_productos);

        progressBar=findViewById(R.id.pbCargandoProductosListBusquedaProductos);
        tvContadorProductos=findViewById(R.id.tvContadorArticulosBusquedaPorProducto);
        etbuscaProductos=findViewById(R.id.etBuscarPorProducto);

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        TIENDA_OBJ = (TiendasModel) bolsa.getSerializable("TIENDA_OBJ");
        PK_TIENDA=TIENDA_OBJ.PK;

        URL_API=getString(R.string.URL_HOST)+"ProductosByTienda/getProductosByTiendaAndNombre";

        listaProductos=new ArrayList<>();

        gridView = findViewById(R.id.listaProductosBusquedaProductos);
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
        //obtenerProductosPortiendayNombreArticulo();

        etbuscaProductos.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    obtenerProductosPortiendayNombreArticulo();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(etbuscaProductos.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
                /*if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    obtenerProductosPortiendayNombreArticulo();
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;*/
            }
        });



        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);

    }

    public void obtenerProductosPortiendayNombreArticulo(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_TIENDA", PK_TIENDA);
            datos.put("PRODUCTO", etbuscaProductos.getText());
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

                                listaProductos.clear();

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
                                }
                                //productosAdapter.setListaProductos(listaProductos);
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

}
