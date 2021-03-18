package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.gesdes.acmarket.adapters.ProductoCategoriaAdapter;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiposTiendasModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductosPorCategoriaActivity extends AppCompatActivity {


    List<ProductosModel>Productos;
    ProductoCategoriaAdapter productoCategoriaAdapter;
    String URL_API;
    ListView listView;
    ImageView ivCategoria;
    TextView tvTitle;
    CategoriasModel CATEGORIA;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_por_categoria);

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        CATEGORIA = (CategoriasModel) bolsa.getSerializable("CATEGORIA");
        URL_API=getString(R.string.URL_HOST)+"ProductosByCategoria";


        progressBar=findViewById(R.id.pbCargandoProductosPorCategoria);
        listView=findViewById(R.id.listViewProductosPorCategoria);
        tvTitle=findViewById(R.id.tvTituloCategoriaActivity);
        ivCategoria=findViewById(R.id.ivImagenCategoriaActivity);

        if(!CATEGORIA.IMAGEN.isEmpty() ){
            Picasso.with(this).load(CATEGORIA.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivCategoria);
        }

        tvTitle.setText(""+CATEGORIA.CLASIFICACION);

        Productos=new ArrayList<>();
        productoCategoriaAdapter=new ProductoCategoriaAdapter(Productos,this);
        listView.setAdapter(productoCategoriaAdapter);

        obtenerProductosPorCategoria();

    }

    public void obtenerProductosPorCategoria(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos=new JSONObject();
        try {
            datos.put("PK_CATEGORIA",CATEGORIA.PK);
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

                                JSONArray promos=response.getJSONArray("productos");
                                Productos.clear();
                                ProductosModel productom;
                                for (int i=0;i<promos.length();i++) {
                                    JSONObject pr= promos.getJSONObject(i);
                                    productom=new ProductosModel();
                                    productom.PK=pr.getString("pk");
                                    productom.PK_CATEGORIA=pr.getString("pK_CATEGORIA");
                                    productom.CATEGORIA=pr.getString("categoria");
                                    productom.PK_TIENDA=pr.getString("pK_TIENDA");
                                    productom.TIENDA=pr.getString("tienda");
                                    productom.PK_TIPO=pr.getString("pK_TIPO_TIENDA");
                                    productom.TIPO=pr.getString("tipo");
                                    productom.IMAGEN_TIPO=pr.getString("imageN_TIPO");
                                    productom.PRODUCTO=pr.getString("producto");
                                    productom.DESCRIPCION=pr.getString("descripcion");
                                    productom.STOCK=pr.getInt("stock");
                                    productom.PK_MEDIDA=pr.getString("pK_MEDIDA");
                                    productom.MEDIDA=pr.getString("medida");
                                    productom.MEDIDA_DESCRIPCION=pr.getString("medidA_DESCRIPCION");
                                    productom.IMAGEN=pr.getString("imagen");
                                    productom.IMAGEN_TIENDA=pr.getString("imageN_TIENDA");
                                    productom.PRECIO=pr.getDouble("precio");
                                    productom.TIENDA_LUNES=pr.getString("tiendA_LUNES");
                                    productom.TIENDA_MARTES=pr.getString("tiendA_MARTES");
                                    productom.TIENDA_MIERCOLES=pr.getString("tiendA_MIERCOLES");
                                    productom.TIENDA_JUEVES=pr.getString("tiendA_JUEVES");
                                    productom.TIENDA_VIERNES=pr.getString("tiendA_VIERNES");
                                    productom.TIENDA_SABADO=pr.getString("tiendA_SABADO");
                                    productom.TIENDA_DOMINGO=pr.getString("tiendA_DOMINGO");
                                    productom.ENTREGA_LUNES=pr.getString("entregA_LUNES");
                                    productom.ENTREGA_MARTES=pr.getString("entregA_MARTES");
                                    productom.ENTREGA_MIERCOLES=pr.getString("entregA_MIERCOLES");
                                    productom.ENTREGA_JUEVES=pr.getString("entregA_JUEVES");
                                    productom.ENTREGA_VIERNES=pr.getString("entregA_VIERNES");
                                    productom.ENTREGA_SABADO=pr.getString("entregA_SABADO");
                                    productom.ENTREGA_DOMINGO=pr.getString("entregA_DOMINGO");
                                    productom.ENTREGA_EXPRESS=pr.getString("entregA_EXPRESS");
                                    Productos.add(productom);
                                }
                                productoCategoriaAdapter.notifyDataSetChanged();
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

}
