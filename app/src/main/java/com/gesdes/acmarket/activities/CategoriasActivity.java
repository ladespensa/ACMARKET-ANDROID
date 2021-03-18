package com.gesdes.acmarket.activities;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.ImageView;
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
import com.gesdes.acmarket.adapters.CategoriasAdapter;
import com.gesdes.acmarket.adapters.TiendasAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity implements TextWatcher {

    GridView gridView;
    CategoriasAdapter categoriasAdapter;
    TiendasModel tienda;
    String PK_TIENDA,URL_API,PK_TIPO,TIPO,NOMBRE_TIENDA,IMAGEN_TIPO,IMAGEN_TIENDA;
    AppDatabase db;
    TextView tvNombre,tvLocalizacion,tvContadorCategorias;
    ImageView ivTienda;
    EditText etCategorias;
    List<CategoriasModel>categoriasLista2;
    ProgressBar progressBar;
    TiendasModel TIENDA_OBJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        progressBar=findViewById(R.id.pbCargandoCategoriasActivityList);
        tvContadorCategorias=findViewById(R.id.tvContadorProductosCategorias);

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        PK_TIENDA = bolsa.getString("PK_TIENDA");
        NOMBRE_TIENDA = bolsa.getString("TIENDA");
        IMAGEN_TIENDA = bolsa.getString("IMAGEN_TIENDA");
        PK_TIPO = bolsa.getString("PK_TIPO");
        TIPO = bolsa.getString("TIPO");
        IMAGEN_TIPO = bolsa.getString("IMAGEN_TIPO");
        TIENDA_OBJ=(TiendasModel) bolsa.getSerializable("TIENDA_OBJ");
        URL_API=getString(R.string.URL_HOST)+"ObtenerCategoriasByPkTipo";

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        gridView = findViewById(R.id.listaCategoriasCategoriasList);
        tienda=new TiendasModel();
        tienda.CATEGORIAS=new ArrayList<>();
        categoriasLista2=new ArrayList<>();
        categoriasAdapter = new CategoriasAdapter(this, tienda.CATEGORIAS);
        gridView.setAdapter(categoriasAdapter);

        ivTienda=findViewById(R.id.ivTiendaCategoras);

        gridView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PedidoModel pedido=new PedidoModel();
                pedido.PK_TIENDA=PK_TIENDA;
                pedido.TIENDA=NOMBRE_TIENDA;
                pedido.IMAGEN_TIENDA=IMAGEN_TIENDA;
                pedido.PK_TIPO=PK_TIPO;
                pedido.TIPO=TIPO;
                pedido.IMAGEN_TIPO=IMAGEN_TIPO;
                pedido.PK_CATEGORIA=tienda.CATEGORIAS.get(position).PK;
                pedido.CLASIFICACION=tienda.CATEGORIAS.get(position).CLASIFICACION;
                Bundle bolsa=new Bundle();
                bolsa.putSerializable("pedido", pedido);
                bolsa.putSerializable("TIENDA_OBJ", TIENDA_OBJ);
                Intent intento=new Intent(getApplicationContext(), ProductosListActivity.class);
                intento.putExtra("bolsa",bolsa);
                startActivity(intento);
            }
        });

        tvNombre=findViewById(R.id.tvNombreCategoriasList);
        tvLocalizacion=findViewById(R.id.tvTitleTiendasCategoriasList);
        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        String nombre=preferences.getString("NOMBRE","");
        String direccion=preferences.getString("Direccion","");
        tvNombre.setText(NOMBRE_TIENDA);
        tvLocalizacion.setText(TIPO+" - Categorías");
        if(IMAGEN_TIPO != null && !IMAGEN_TIPO.isEmpty() ){
            Picasso.with(this).load(IMAGEN_TIPO)
                    .transform(new RoundedTransformation(50, 0))
                    .placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivTienda);
        }

        etCategorias=findViewById(R.id.etBuscarCategorias);
        etCategorias.addTextChangedListener(this);

        obtenerCategoriasTiendas();
    }

    public void obtenerCategoriasTiendas(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_TIPO_TIENDA", Long.valueOf( PK_TIPO));
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
                                JSONArray categorias=response.getJSONArray("categorias");
                                CategoriasModel aux;
                                if(tienda.CATEGORIAS==null){
                                    tienda.CATEGORIAS=new ArrayList<>();
                                }
                                for (int i=0;i<categorias.length();i++) {
                                    JSONObject categoria= categorias.getJSONObject(i);
                                    aux=new CategoriasModel();
                                    aux.PK=categoria.getString("pk");
                                    aux.CLASIFICACION=categoria.getString("clasificacion");
                                    aux.IMAGEN=categoria.getString("imagen");
                                    aux.DESCRIPCION=categoria.getString("descripcion");
                                    aux.BORRADO=categoria.getString("borrado");
                                    tienda.CATEGORIAS.add(aux);
                                    categoriasLista2.add(aux);
                                }
                                //categoriasAdapter.setListaTipos(tienda.CATEGORIAS);
                                categoriasAdapter.notifyDataSetChanged();

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

    private void _ShowAlertOption(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        db.PedidoDetalleDao().deleteAllProductos();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Si cambias de tienda se borrara tu pedido actual deseas eliminar productos?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /*
    @Override
    public void onBackPressed() {

        //_ShowAlertOption();
        //super.onBackPressed();

    }*/

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
        tienda.CATEGORIAS.clear();
        for(CategoriasModel categoria : categoriasLista2){
            if(categoria.CLASIFICACION.toLowerCase().contains(s)){
                tienda.CATEGORIAS.add(categoria);
            }
        }
        categoriasAdapter.notifyDataSetChanged();

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onStart() {
        super.onStart();
        List<PedidoDetalleModel> pedidos = db.PedidoDetalleDao().getAll();
        //Toast.makeText(this, "Cantidad " + pedidos.size(), Toast.LENGTH_LONG).show();
        tvContadorCategorias.setText(""+pedidos.size());

    }


}
