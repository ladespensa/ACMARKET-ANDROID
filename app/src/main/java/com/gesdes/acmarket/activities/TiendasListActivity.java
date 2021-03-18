package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.adapters.PromocionesAdapter;
import com.gesdes.acmarket.adapters.TiendasAdapter;
import com.gesdes.acmarket.adapters.TiposTiendaAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.ClienteModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.model.TiposTiendasModel;
import com.gesdes.acmarket.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TiendasListActivity extends AppCompatActivity implements  TextWatcher {

    GridView gridView;
    List<TiposTiendasModel> listaTiposTiendas,listaTiposTiendas2;
    TiposTiendaAdapter tiposTiendasAdapter;
    String URL_API;
    String PK_TIENDA,TIENDA,IMAGEN_TIENDA;
    TextView tvNombre,tvTitle;
    EditText etBuscar;
    ProgressBar progressBar;
    AppDatabase db;
    TiendasModel TIENDA_OBJ;
    List<ProductosModel>Promociones;
    PromocionesAdapter promoAdapter;
    RecyclerView lvPromociones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas_list);
        URL_API=getString(R.string.URL_HOST)+"ObtenerTiposTiendasByPkTienda";

        db = Room.databaseBuilder(TiendasListActivity.this,
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();


        progressBar = findViewById(R.id.pbCargandoTiendasListActivity);
        gridView = findViewById(R.id.listaTiendasTiendasList);
        listaTiposTiendas=new ArrayList<>();
        listaTiposTiendas2=new ArrayList<>();
        tiposTiendasAdapter = new TiposTiendaAdapter(this, listaTiposTiendas);
        gridView.setAdapter(tiposTiendasAdapter);

        tvNombre=findViewById(R.id.tvNombreTiendasList);
        tvTitle=findViewById(R.id.tvTitleTiendasTiendasList);
        gridView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String pk = listaTiposTiendas.get(position).PK;
                    String imagen = listaTiposTiendas.get(position).IMAGEN;
                    String nombre = listaTiposTiendas.get(position).TIPO;
                    Bundle bolsa = new Bundle();
                    bolsa.putString("PK_TIENDA", PK_TIENDA);
                    bolsa.putString("TIENDA", TIENDA);
                    bolsa.putString("IMAGEN_TIENDA", IMAGEN_TIENDA);
                    bolsa.putString("PK_TIPO", pk);
                    bolsa.putString("TIPO", nombre);
                    bolsa.putString("IMAGEN_TIPO", imagen);
                    bolsa.putSerializable("TIENDA_OBJ", TIENDA_OBJ);

                    Intent intento = new Intent(TiendasListActivity.this, CategoriasActivity.class);
                    intento.putExtra("bolsa", bolsa);
                    startActivity(intento);

            }
        });

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        PK_TIENDA = bolsa.getString("PK_TIENDA");
        TIENDA = bolsa.getString("TIENDA");
        IMAGEN_TIENDA = bolsa.getString("IMAGEN_TIENDA");
        TIENDA_OBJ=(TiendasModel) bolsa.getSerializable("TIENDA_OBJ");

        LinearLayoutManager layoutManager= new LinearLayoutManager(TiendasListActivity.this,LinearLayoutManager.HORIZONTAL, false);
        lvPromociones = findViewById(R.id.lvPromociones);
        lvPromociones.setLayoutManager(layoutManager);

        Promociones=new ArrayList<>();
        promoAdapter=new PromocionesAdapter(TiendasListActivity.this,Promociones);
        lvPromociones.setAdapter(promoAdapter);
        lvPromociones.setHasFixedSize(true);

        obtenerTiposTiendas();

        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        String nombre=preferences.getString("NOMBRE","");
        String direccion=preferences.getString("Direccion","");
        //tvNombre.setText(nombre);
        tvTitle.setText(TIENDA);

        etBuscar=findViewById(R.id.etBuscarTienda);
        etBuscar.addTextChangedListener(this);

    }

    public void obtenerTiposTiendas(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos=new JSONObject();
        try {
            datos.put("PK",PK_TIENDA);
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
                                JSONArray tipos=response.getJSONArray("tipos");
                                TiposTiendasModel aux;

                                listaTiposTiendas.clear();
                                listaTiposTiendas2.clear();

                                for (int i=0;i<tipos.length();i++) {
                                    JSONObject tipo= tipos.getJSONObject(i);
                                    aux=new TiposTiendasModel();
                                    aux.PK=tipo.getString("pk");
                                    aux.TIPO=tipo.getString("tipo");
                                    aux.IMAGEN=tipo.getString("imagen");
                                    aux.BORRADO=tipo.getString("borrado");
                                    listaTiposTiendas.add(aux);
                                    listaTiposTiendas2.add(aux);
                                }
                                tiposTiendasAdapter.notifyDataSetChanged();

                                JSONArray promos=response.getJSONArray("promos");
                                Promociones.clear();
                                ProductosModel productom;
                                for (int i=0;i<promos.length();i++) {
                                    JSONObject pr= promos.getJSONObject(i);
                                    productom=new ProductosModel();
                                    productom.PK=pr.getString("pk");
                                    productom.PK_CATEGORIA=pr.getString("pK_CATEGORIA");
                                    productom.CATEGORIA=pr.getString("categoria");
                                    productom.IMAGEN_CATEGORIA=pr.getString("imageN_CATEGORIA");
                                    productom.PK_TIENDA=pr.getString("pK_TIENDA");
                                    productom.TIENDA=pr.getString("tienda");
                                    productom.PK_TIPO=pr.getString("pK_TIPO_TIENDA");
                                    productom.TIPO=pr.getString("tipo");
                                    productom.PRODUCTO=pr.getString("producto");
                                    productom.DESCRIPCION=pr.getString("descripcion");
                                    productom.STOCK=pr.getInt("stock");
                                    productom.PK_MEDIDA=pr.getString("pK_MEDIDA");
                                    productom.MEDIDA=pr.getString("medida");
                                    productom.MEDIDA_DESCRIPCION=pr.getString("medidA_DESCRIPCION");
                                    productom.IMAGEN=pr.getString("imagen");
                                    productom.IMAGEN_TIENDA=pr.getString("imageN_TIENDA");
                                    productom.IMAGEN_TIPO=pr.getString("imageN_TIPO");
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

                                    Promociones.add(productom);
                                }
                                promoAdapter.notifyDataSetChanged();


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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String s=charSequence.toString().toLowerCase();
        listaTiposTiendas.clear();
        for(TiposTiendasModel tipoTienda : listaTiposTiendas2){
            if(tipoTienda.TIPO.toLowerCase().contains(s)){
                listaTiposTiendas.add(tipoTienda);
            }
        }
        tiposTiendasAdapter.notifyDataSetChanged();

    }

    @Override
    public void afterTextChanged(Editable editable) {

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

    @Override
    public void onBackPressed() {

        _ShowAlertOption();
        //super.onBackPressed();

    }

    public void buscaArticulo(View view){


        Bundle bolsa = new Bundle();
        bolsa.putString("PK_TIENDA", PK_TIENDA);
        bolsa.putString("TIENDA", TIENDA);
        bolsa.putString("IMAGEN_TIENDA", IMAGEN_TIENDA);
        bolsa.putString("PK_TIPO", "");
        bolsa.putString("TIPO", "");
        bolsa.putString("IMAGEN_TIPO", "");
        bolsa.putSerializable("TIENDA_OBJ", TIENDA_OBJ);

        Intent intento = new Intent(TiendasListActivity.this, BusquedaProductosActivity.class);
        intento.putExtra("bolsa", bolsa);
        startActivity(intento);

    }

}
