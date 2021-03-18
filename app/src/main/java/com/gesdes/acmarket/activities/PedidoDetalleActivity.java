package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.gesdes.acmarket.adapters.PedidosAdapter;
import com.gesdes.acmarket.adapters.ProductosCarritoAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.PedidoDetalleDao;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductoPedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PedidoDetalleActivity extends AppCompatActivity {

    ListView listaView;
    TextView tvTarifa,tvTotal,tvComision;
    List<ProductosModel>listaPedido;
    ProductosCarritoAdapter productosAdapter;
    AppDatabase db;
    PedidoModel pedido;
    TiendasModel TiendaObj;
    String URL_API;
    ProgressBar progressBar;
    Button btnVolverAPedir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_detalle);

        progressBar=findViewById(R.id.pbCargandoPedidoDetalleActivity);
        btnVolverAPedir=findViewById(R.id.btnVolverAPedirPedidoDetalle);
        btnVolverAPedir.setVisibility(View.GONE);

        URL_API=getString(R.string.URL_HOST)+"ObtenerTiendaByPk/TiendaLightByPk";
        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        pedido= (PedidoModel)bolsa.getSerializable("pedido");
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        obtenerTiendaObj();

        listaView=findViewById(R.id.listaProductos);
        tvTarifa=findViewById(R.id.tvTarifaPedidoDetalle);
        tvTotal=findViewById(R.id.tvTotalPedidoDetalle);
        tvComision=findViewById(R.id.tvComisionMiPedidoDetalle);

        listaPedido=new ArrayList<>();
        ProductosModel aux;
        double total=0;
        for(PedidoDetalleModel detalle :pedido.LISTA){
            aux=new ProductosModel();
            aux.PRODUCTO=detalle.PRODUCTO;
            aux.PRECIO=detalle.PRECIO;
            aux.CANTIDAD=detalle.CANTIDAD;
            aux.IMAGEN=detalle.IMAGEN;
            total+=(aux.PRECIO*aux.CANTIDAD);
            listaPedido.add(aux);
        }

        productosAdapter = new ProductosCarritoAdapter(this, listaPedido);
        listaView.setAdapter(productosAdapter);

        total+=pedido.PRECIO_ENTREGA;
        productosAdapter = new ProductosCarritoAdapter(this, listaPedido);
            tvTarifa.setText("Tarifa: $"+pedido.PRECIO_ENTREGA);
            tvTotal.setText("Total: $"+pedido.TOTAL);
        tvComision.setText("Comisiòn $"+pedido.COMISION_TARJETA);
    }

    public void volverAPedir(View view){

        for(PedidoDetalleModel detalle :pedido.LISTA){

            PedidoDetalleModel pr=db.PedidoDetalleDao().getProductoByPk(detalle.PK_PRODUCTO);
            if(pr==null || pr.PK_PRODUCTO.isEmpty() || pr.PK_PRODUCTO ==null || pr.PK_PRODUCTO.equals("null")){
                db.PedidoDetalleDao().insertAll(detalle);
            }
            else{
                detalle.PK=pr.PK;
                db.PedidoDetalleDao().update(detalle);
            }

        }

        String pk=pedido.PK_TIENDA;
        String nombre=pedido.TIENDA;
        String imagen=pedido.IMAGEN_TIENDA;
        Bundle bolsa=new Bundle();
        bolsa.putString("PK_TIENDA", pk);
        bolsa.putString("TIENDA", nombre);
        bolsa.putString("IMAGEN_TIENDA", imagen);
        bolsa.putSerializable("TIENDA_OBJ",TiendaObj);
        Intent intento=new Intent(PedidoDetalleActivity.this, TiendasListActivity.class);
        intento.putExtra("bolsa",bolsa);
        startActivity(intento);

        Bundle bolsa1= new Bundle();
        bolsa1.putSerializable("TIENDA_OBJ",TiendaObj);
        Intent intento1=new Intent(this, MiPedidoActivity.class);
        intento1.putExtra("bolsa",bolsa);
        startActivity(intento1);

    }

    public void obtenerTiendaObj(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos=new JSONObject();
        try {
            datos.put("PK",pedido.PK_TIENDA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requstQueue = Volley.newRequestQueue(PedidoDetalleActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }

                            int result = (int) response.get("resultado");

                            if (result == 1) {

                                JSONObject tipo = response.getJSONObject("tienda");
                                TiendaObj = new TiendasModel();
                                TiendaObj.PK = tipo.getString("pk");
                                TiendaObj.NOMBRE = tipo.getString("nombre");
                                TiendaObj.IMAGEN = tipo.getString("imagen");
                                TiendaObj.DIRECCION = tipo.getString("direccion");
                                TiendaObj.LATITUD = tipo.getString("latitud");
                                TiendaObj.LONGITUD = tipo.getString("longitud");
                                TiendaObj.LUNES = tipo.getString("lunes");
                                TiendaObj.MARTES = tipo.getString("martes");
                                TiendaObj.MIERCOLES = tipo.getString("miercoles");
                                TiendaObj.JUEVES = tipo.getString("jueves");
                                TiendaObj.VIERNES = tipo.getString("viernes");
                                TiendaObj.SABADO = tipo.getString("sabado");
                                TiendaObj.DOMINGO = tipo.getString("domingo");
                                TiendaObj.ENTREGA_LUNES = tipo.getString("entregA_LUNES");
                                TiendaObj.ENTREGA_MARTES = tipo.getString("entregA_MARTES");
                                TiendaObj.ENTREGA_MIERCOLES = tipo.getString("entregA_MIERCOLES");
                                TiendaObj.ENTREGA_JUEVES = tipo.getString("entregA_JUEVES");
                                TiendaObj.ENTREGA_VIERNES = tipo.getString("entregA_VIERNES");
                                TiendaObj.ENTREGA_SABADO = tipo.getString("entregA_SABADO");
                                TiendaObj.ENTREGA_DOMINGO = tipo.getString("entregA_DOMINGO");
                                TiendaObj.ENTREGA_EXPRESS = tipo.getString("entregA_EXPRESS");
                                TiendaObj.BORRADO = tipo.getString("borrado");

                                btnVolverAPedir.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
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


}
