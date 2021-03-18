package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.TotalCaptureResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.adapters.CategoriasAdapter;
import com.gesdes.acmarket.adapters.ProductosCarritoAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.fragments.CodigoDescuentoDialog;
import com.gesdes.acmarket.interfaces.SwipeDismissListViewTouchListener;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.CostosModel;
import com.gesdes.acmarket.model.OpenPayApp;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TarjetasModel;
import com.gesdes.acmarket.model.TiendasModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import mx.openpay.android.Openpay;

public class MiPedidoActivity extends AppCompatActivity  implements CodigoDescuentoDialog.CodigoDescuentoDialogListener {

    ProgressBar pbCarcando;
    Spinner spCostos;
    ArrayAdapter<String> comboAdapter,tarjetasAdapter;
    ListView gridView;
    TextView tvPrecioEnvio,tvTotal,tvSubTotal,tvComisionTarjeta;
    ProductosCarritoAdapter productosAdapter;
    String PK_TIENDA,URL_API,URL_API2,URL_API3,USER_OPENID,pkCliente,direccion,latitud,longitud,CARD_ID,CVV2,charid,apiopenpay,METODO_PAGO,PK_COSTO_ENVIO;
    String CODIGO_DESCUENTO="";
    AppDatabase db;
    List<ProductosModel>listaProductos;
    List<CostosModel>listaCostos;
    List<String>listaCostosS;
    double COSTO_ENVIO_DATO=0,COSTO_ENVIO=0,SUBTOTAL=0,COMISION_TARJETA=0,FACTOR=0,SUMA=0,TOTAL=0,FACTOR_DATO=0,SUMA_DATO=0;
    EditText etcvv2;
    Boolean produccion=false;
    TarjetasModel TARJETA;
    Button btnMetodoPago,btnCodigoDescuento;
    ProgressBar progressBar;
    TiendasModel TIENDA_OBJ;
    int DESCUENTO=0;
    int CANTIDAD_MIS_PEDIDOS=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_pedido);


        SharedPreferences preferences =getSharedPreferences("VARIABLES",MODE_PRIVATE);
        pkCliente= preferences.getString("PK",null);
        direccion=preferences.getString("Direccion","");
        latitud=preferences.getString("Lat","");
        longitud=preferences.getString("Lon","");
        USER_OPENID=preferences.getString("OPENID","");
        produccion=preferences.getBoolean("PRODUCCION_OPEN_PAY",false);


        //charid= getString(R.string.merchantId);
        //apiopenpay= getString(R.string.apiOpenPay);
        charid= preferences.getString("ID_OPEN","");
        apiopenpay= preferences.getString("PUBLIC_OPEN_KEY","");
        CANTIDAD_MIS_PEDIDOS= preferences.getInt("CANTIDAD_MIS_PEDIDOS",0);

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        TIENDA_OBJ=(TiendasModel) bolsa.getSerializable("TIENDA_OBJ");
        PK_TIENDA=TIENDA_OBJ.PK;

        progressBar=findViewById(R.id.pbCargandoMiPedidoActivity);
        tvPrecioEnvio=findViewById(R.id.tvPrecioEnvio);
        tvTotal=findViewById(R.id.tvTotal);
        etcvv2=findViewById(R.id.etCVV2MiPedido);
        pbCarcando=findViewById(R.id.pbCargandoPreparandoPedido);
        tvSubTotal=findViewById(R.id.tvSubTotalMiPedido);
        tvComisionTarjeta=findViewById(R.id.tvComisionTarjetaMiPedido);
        btnMetodoPago=findViewById(R.id.btnMetodoPagoCarritoActivity);
        btnCodigoDescuento=findViewById(R.id.btnCodigoOpen);

        listaProductos=new ArrayList<>();
        listaCostos=new ArrayList<>();
        listaCostosS=new ArrayList<>();

        URL_API=getString(R.string.URL_HOST)+"ProductosListDetalle1";
        URL_API2=getString(R.string.URL_HOST)+"CostosEnviosList";
        URL_API3=getString(R.string.URL_HOST)+"ListaTarjetasClientesByPkCliente";
        //produccion=Boolean.parseBoolean(getString(R.string.OPEN_PRODUCCION));



        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        spCostos=findViewById(R.id.spCostos);
        Collections.addAll(listaCostos);
        comboAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, listaCostosS);
        spCostos.setAdapter(comboAdapter);

        TARJETA=new TarjetasModel();
        /*
        TARJETA.ID="EFECTIVO";
        TARJETA.BANK_NAME="EFECTIVO";
        TARJETA.BRAND="EFECTIVO";
        TARJETA.CARD_NUMBRE="EFECTIVO";
        CARD_ID=TARJETA.ID;
        METODO_PAGO="E";
        btnMetodoPago.setText(""+TARJETA.CARD_NUMBRE);
        Drawable da=getResources().getDrawable(R.drawable.money);
        btnMetodoPago.setCompoundDrawablesWithIntrinsicBounds(da,null,null,null);
        */
        etcvv2.setVisibility(View.GONE);
        tvComisionTarjeta.setVisibility(View.GONE);


        List<PedidoDetalleModel> pedidos0= db.PedidoDetalleDao().getAll();
        for (PedidoDetalleModel pedido : pedidos0){
            ProductosModel aux1=new ProductosModel();
            aux1.PK=pedido.PK_PRODUCTO;
            aux1.PRODUCTO=pedido.PRODUCTO;
            aux1.PRECIO=pedido.PRECIO;
            aux1.CANTIDAD=pedido.CANTIDAD;
            aux1.IMAGEN="";
            listaProductos.add(aux1);
        }
        calculaTotal();

        gridView = findViewById(R.id.listaProductos);
        productosAdapter = new ProductosCarritoAdapter(this, listaProductos);
        gridView.setAdapter(productosAdapter);



        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        gridView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    ProductosModel producto=listaProductos.get(position);
                                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                            AppDatabase.class, "polar-base").allowMainThreadQueries().build();
                                    db.PedidoDetalleDao().deleteProductosByPk(producto.PK);
                                    listaProductos.remove(producto);
                                    productosAdapter.notifyDataSetChanged();
                                    calculaTotal();
                                }
                            }
                        });
        gridView.setOnTouchListener(touchListener);
/*

        gridView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ImageButton btnDelete= view.findViewById(R.id.ivBoteProductoItem);


                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductosModel producto=listaProductos.get(position);
                        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "polar-base").allowMainThreadQueries().build();
                        db.PedidoDetalleDao().deleteProductosByPk(producto.PK);
                        listaProductos.remove(producto);
                        productosAdapter.notifyDataSetChanged();

                    }
                });
            }
        });*/

        obtenerProductos();
        obtenerCostosEntrega();
        spCostos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DecimalFormat df = new DecimalFormat("#0.00");
                tvPrecioEnvio.setText("Costo envío: $"+df.format(listaCostos.get(position).COSTO));
                COSTO_ENVIO=listaCostos.get(position).COSTO;
                PK_COSTO_ENVIO=listaCostos.get(position).PK;
                COSTO_ENVIO_DATO=COSTO_ENVIO;
                calculaTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if(CANTIDAD_MIS_PEDIDOS==0) {//solo si no ha hecho pedidos le ponesmos descuento 100% en el primer pedido
            //cambiaTexto("PRIMER", "", 1, 100);
            DESCUENTO=100;
            CODIGO_DESCUENTO="PRIMERO";
            btnCodigoDescuento.setText("PRIMERO 100% descuento en envío");//primer pedido
            calculaTotal();
        }
        /*
        spTarjetas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                CARD_ID=listaTarjetas.get(position).ID;
                if(CARD_ID.equals("EFECTIVO")){
                    METODO_PAGO="E";
                    etcvv2.setVisibility(View.GONE);
                    tvComisionTarjeta.setVisibility(View.GONE);
                    FACTOR=0;
                    SUMA=0;
                }else{
                    METODO_PAGO="T";
                    etcvv2.setVisibility(View.VISIBLE);
                    tvComisionTarjeta.setVisibility(View.VISIBLE);
                    FACTOR=FACTOR_DATO;
                    SUMA=SUMA_DATO;
                }
                calculaTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        */
    }

    public void comprar(View view){
        //registraProductos();
        preguntaDireccion();
    }

    public void obtenerProductos(){

        progressBar.setVisibility(View.VISIBLE);

        JSONArray arreglo=new JSONArray();

        List<String> pedidos= db.PedidoDetalleDao().getAllPks();
        for (String pedido : pedidos){
            JSONObject objeto=new JSONObject();
            try {
                objeto.put("PK",String.valueOf(pedido));
            }catch (JSONException e) {
                e.printStackTrace();
            }
            arreglo.put(objeto);
        }



        JSONObject datos = new JSONObject();
        try {
            datos.put("productosList", arreglo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressBar!=null){
                                progressBar.setVisibility(View.GONE);
                            }
                            int result = (int) response.get("resultado");

                            if(result == 1){
                                FACTOR= response.getDouble("factor");
                                SUMA= response.getDouble("suma");
                                FACTOR_DATO=FACTOR;
                                SUMA_DATO=SUMA;
                                JSONArray productos=response.getJSONArray("productos");
                                ProductosModel aux;
                                if(listaProductos==null){
                                    listaProductos=new ArrayList<>();
                                }
                                listaProductos.clear();
                                for (int i=0;i<productos.length();i++) {
                                    JSONObject producto= productos.getJSONObject(i);
                                    aux=new ProductosModel();
                                    aux.PK=producto.getString("pk");
                                    PK_TIENDA=producto.getString("pK_TIENDA");
                                    aux.PRODUCTO=producto.getString("producto");
                                    aux.IMAGEN=producto.getString("imagen");
                                    aux.DESCRIPCION=producto.getString("descripcion");
                                    aux.BORRADO=producto.getString("borrado");
                                    aux.PRECIO=producto.getDouble("precio");
                                    aux.CANTIDAD=db.PedidoDetalleDao().getCantidadFromProducto(aux.PK);
                                    listaProductos.add(aux);
                                }
                                productosAdapter.setListaProductos(listaProductos);
                                productosAdapter.notifyDataSetChanged();

                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("¡Error intente nuevamente!",error,1);

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
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && !networkInfo.isConnected()) {
                            _ShowAlert("¡Error intente nuevamente!","Verifique su conexiòn de internet",1);
                        } else {
                            _ShowAlert("¡Error intente nuevamente!","¡Ocurrio un error al cargar el detalle del pedido!",1);
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

    public void obtenerCostosEntrega(){

        progressBar.setVisibility(View.VISIBLE);

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressBar!=null){
                                progressBar.setVisibility(View.GONE);
                            }

                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONArray costos=response.getJSONArray("costos");
                                CostosModel aux;
                                if(listaCostos==null){
                                    listaCostos=new ArrayList<>();
                                }
                                listaCostosS.clear();
                                for (int i=0;i<costos.length();i++) {
                                    JSONObject producto= costos.getJSONObject(i);
                                    aux=new CostosModel();
                                    aux.PK=producto.getString("pk");
                                    aux.DESCRIPCION=producto.getString("descripcion");
                                    aux.COSTO=producto.getDouble("costo");
                                    aux.BORRADO=producto.getString("borrado");
                                    listaCostos.add(aux);
                                    listaCostosS.add(aux.DESCRIPCION);
                                }
                                //comboAdapter.addAll(listaCostosS);
                                comboAdapter.notifyDataSetChanged();
                                calculaTotal();

                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("¡Error intente nuevamente!",error,1);

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
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && !networkInfo.isConnected()) {
                            _ShowAlert("¡Error intente nuevamente!","Verifique su conexiòn de internet",1);
                        } else {
                            _ShowAlert("¡Error intente nuevamente!","¡Ocurrio un error al obtener los costos de envío!",1);
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

    public void obtenerTarjetas(){
/*
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo tarjetas...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        JSONObject datos=new JSONObject();
        try{
            datos.put("PK_CLIENTE",pkCliente);
        }catch (Exception e){}

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API3, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();

                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONArray tarjetas=response.getJSONArray("tarjetas");
                                TarjetasModel aux;
                                if(listaTarjetas==null){
                                    listaTarjetas=new ArrayList<>();
                                }
                                listaTarjetas.clear();
                                listaTarjetasS.clear();
                                aux=new TarjetasModel();
                                aux.ID="EFECTIVO";
                                aux.CARD_NUMBRE="EFECTIVO";
                                listaTarjetas.add(aux);
                                listaTarjetasS.add(aux.CARD_NUMBRE);
                                for (int i=0;i<tarjetas.length();i++) {
                                    JSONObject tarjeta= tarjetas.getJSONObject(i);
                                    aux=new TarjetasModel();
                                    aux.ID=tarjeta.getString("id");
                                    aux.BANK_NAME=tarjeta.getString("bankName");
                                    aux.BRAND=tarjeta.getString("brand");
                                    aux.CARD_NUMBRE=tarjeta.getString("cardNumber");
                                    listaTarjetas.add(aux);
                                    listaTarjetasS.add(aux.BRAND+" "+ aux.CARD_NUMBRE.substring(12));
                                }
                                //comboAdapter.addAll(listaCostosS);
                                tarjetasAdapter.notifyDataSetChanged();

                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("¡Error intente nuevamente!",error,0);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && !networkInfo.isConnected()) {
                            _ShowAlert("¡Error intente nuevamente!","Verifique su conexiòn de internet",0);
                        } else {
                            _ShowAlert("¡Error intente nuevamente!","¡Ocurrio un error al cargar sus tarjetas!",0);
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
*/
    }

    public void registraProductos(){

        progressBar.setVisibility(View.VISIBLE);

        OpenPayApp open;
        Openpay openpay;
        String deviceIdString="ID_DEVICE";

        if(CARD_ID==null || CARD_ID.isEmpty()){
            btnMetodoPago.setError("Selecciona una forma de pago");
            if(progressBar!=null){
                progressBar.setVisibility(View.GONE);
            }
            return;
        }
        else if(!CARD_ID.equals("EFECTIVO") && !CARD_ID.equals("TERMINAL")){
            open = new OpenPayApp(charid, apiopenpay, produccion);
            openpay = open.getOpenpay();
            deviceIdString = openpay.getDeviceCollectorDefaultImpl().setup(this);
        }
        CVV2 = etcvv2.getText().toString();

        JSONArray arreglo=new JSONArray();

        final List<PedidoDetalleModel> pedidos= db.PedidoDetalleDao().getAll();
        for (PedidoDetalleModel pedido : pedidos){
            JSONObject objeto=new JSONObject();
            try {
                objeto.put("PK_PRODUCTO",pedido.PK_PRODUCTO);
                objeto.put("PRECIO",pedido.PRECIO);
                objeto.put("CANTIDAD",pedido.CANTIDAD);
                objeto.put("DETALLES",pedido.DETALLES);
            }catch (JSONException e) {
                e.printStackTrace();
            }
            arreglo.put(objeto);
        }

        Double entrega=COSTO_ENVIO;
        Double comision=COMISION_TARJETA;
        Double subtotal1=SUBTOTAL;
        Double total1=TOTAL;

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_CLIENTE", pkCliente);
            datos.put("PK_TIENDA", PK_TIENDA);
            datos.put("DIRECCION", direccion);
            datos.put("LATITUD", latitud);
            datos.put("LONGITUD", longitud);
            datos.put("ENVIO", entrega);
            datos.put("SUBTOTAL", subtotal1);
            datos.put("COMISION_TARJETA", comision);
            datos.put("TOTAL", total1);
            datos.put("METODO_PAGO", METODO_PAGO);
            datos.put("LISTA", arreglo);
            datos.put("SOURCE_ID",CARD_ID);
            datos.put("DEVICE_SESSION_ID",deviceIdString);
            datos.put("CVV2",CVV2);
            datos.put("COSTUMER_ID",USER_OPENID);
            datos.put("PK_COSTO_ENVIO",PK_COSTO_ENVIO);
            if(!CODIGO_DESCUENTO.isEmpty()){
                datos.put("CODIGO_DESCUENTO",CODIGO_DESCUENTO);
                datos.put("DESCUENTO",DESCUENTO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(CARD_ID!=null && !CARD_ID.equals("EFECTIVO")&& !CARD_ID.equals("TERMINAL") ) {
            if (CVV2==null || CVV2.isEmpty()) {
                etcvv2.setError("El cvv2 es obligatorio");
                if(progressBar!=null){
                    progressBar.setVisibility(View.GONE);
                }
                return;
            }
        }

        Bundle bolsa=new Bundle();
        bolsa.putString("datos",datos.toString());
        bolsa.putSerializable("TIENDA_OBJ",TIENDA_OBJ);
        Intent intento=new Intent(this,ConfirmaDireccionActivity.class);
        intento.putExtra("bolsa",bolsa);
        startActivity(intento);
        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }

    }


    private void _ShowAlert(String title, String mensaje, final int opc){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(opc==1){
                            finish();
                        }
                    }
                });
        alertDialog.show();
    }

    public static double fijarNumero(double numero, int digitos) {
        double resultado;
        resultado = numero * Math.pow(10, digitos);
        resultado = Math.round(resultado);
        resultado = resultado/Math.pow(10, digitos);
        return resultado;
    }

    public void calculaTotal(){
        TOTAL=0;
        COMISION_TARJETA=0;
        SUBTOTAL=0;

        COSTO_ENVIO=COSTO_ENVIO_DATO-(COSTO_ENVIO_DATO*DESCUENTO/100);
        DecimalFormat df = new DecimalFormat("#0.00");
        tvPrecioEnvio.setText("Costo envío: $"+df.format(COSTO_ENVIO));

        for (ProductosModel producto : listaProductos){
            TOTAL+=producto.CANTIDAD*producto.PRECIO;
        }
        SUBTOTAL=fijarNumero(TOTAL,2);
        TOTAL=COSTO_ENVIO+SUBTOTAL;
        if(TARJETA==null || TARJETA.ID==null){
            COMISION_TARJETA=0;
        }else if(TARJETA!=null && TARJETA.ID!=null && !TARJETA.ID.equals("EFECTIVO")&& !TARJETA.ID.equals("TERMINAL")){
            if(FACTOR>0){
                COMISION_TARJETA=TOTAL*FACTOR;
            }
            COMISION_TARJETA+=SUMA;
        }
        COMISION_TARJETA=fijarNumero(COMISION_TARJETA,2);
        TOTAL+= COMISION_TARJETA;
        TOTAL=fijarNumero(TOTAL,2);

        tvSubTotal.setText("Subtotal $"+df.format(SUBTOTAL));
        tvComisionTarjeta.setText("Comisión $"+df.format(COMISION_TARJETA));
        tvTotal.setText("Total $"+df.format(TOTAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerTarjetas();
    }

    int REQUEST_CODE_DIRECCION = 1;
    int REQUEST_CODE_TARJETA = 101;

    public void preguntaDireccion(){

        progressBar.setVisibility(View.VISIBLE);

        if(CARD_ID==null || CARD_ID.isEmpty()){
            btnMetodoPago.setError("Selecciona una forma de pago");
            if(progressBar!=null){
                progressBar.setVisibility(View.GONE);
            }
            return;
        }
        else if(CARD_ID!=null && !CARD_ID.equals("EFECTIVO")&& !CARD_ID.equals("TERMINAL")) {
            CVV2=etcvv2.getText().toString();
            if (CVV2.isEmpty()) {
                etcvv2.setError("El cvv2 es obligatorio");
                if(progressBar!=null){
                    progressBar.setVisibility(View.GONE);
                }
                return;
            }
        }


        Intent intento=new Intent(this,DireccionesActivity.class);
        startActivityForResult(intento,REQUEST_CODE_DIRECCION);

    }

    public void preguntaTarjeta(View view){

        progressBar.setVisibility(View.VISIBLE);
        Intent intento=new Intent(this,TarjetasActivity.class);
        startActivityForResult(intento,REQUEST_CODE_TARJETA);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }
        if (requestCode == REQUEST_CODE_DIRECCION) {
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                try {
                    JSONObject obj=new JSONObject(returnedResult);
                    direccion=obj.getString("DIRECCION");
                    if(!direccion.equals("Mi ubicaciòn")){
                        latitud=obj.getString("LATITUD");
                        longitud=obj.getString("LONGITUD");
                    }else{
                        latitud="-1";
                        longitud="-1";
                    }
                    registraProductos();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode==REQUEST_CODE_TARJETA){
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                try {
                    JSONObject obj=new JSONObject(returnedResult);
                    TARJETA.ID=obj.getString("ID");
                    TARJETA.BANK_NAME=obj.getString("BANK_NAME");
                    TARJETA.BRAND=obj.getString("BRAND");
                    TARJETA.CARD_NUMBRE=obj.getString("CARD_NUMBRE");
                    CARD_ID=TARJETA.ID;
                    btnMetodoPago.setText(""+TARJETA.CARD_NUMBRE);
                    Drawable da=null;
                    if(CARD_ID.equals("EFECTIVO")){
                        METODO_PAGO="E";
                        etcvv2.setVisibility(View.GONE);
                        tvComisionTarjeta.setVisibility(View.GONE);
                        FACTOR=0;
                        SUMA=0;
                        da=getResources().getDrawable(R.drawable.money);
                    }else if(CARD_ID.equals("TERMINAL")){
                        METODO_PAGO="C";
                        etcvv2.setVisibility(View.GONE);
                        tvComisionTarjeta.setVisibility(View.GONE);
                        FACTOR=0;
                        SUMA=0;
                        da=getResources().getDrawable(R.drawable.terminal_24);
                    }else{
                        METODO_PAGO="T";
                        etcvv2.setVisibility(View.VISIBLE);
                        tvComisionTarjeta.setVisibility(View.VISIBLE);
                        FACTOR=FACTOR_DATO;
                        SUMA=SUMA_DATO;
                        if(TARJETA.BRAND.toLowerCase().equals("visa")) {
                            da = getResources().getDrawable(R.drawable.visa_24w);
                        }else {
                            da = getResources().getDrawable(R.drawable.mastercard_24);
                        }
                    }
                    btnMetodoPago.setCompoundDrawablesWithIntrinsicBounds(da,null,null,null);
                    calculaTotal();
                    btnMetodoPago.setError(null);
                }catch (Exception e){}
            }
        }
    }
    CodigoDescuentoDialog codigoFragment;
    public void AbreFragmentCodigoDescuento(View view){
        codigoFragment=new CodigoDescuentoDialog();
        codigoFragment.show(getSupportFragmentManager(),"Ingresar código de descuento");
    }

    @Override
    public void cambiaTexto(String codigo, String Error, int resultado,int porcentaje) {
        if(codigoFragment != null){
            codigoFragment.dismiss();
        }
        if(resultado==1){
            DESCUENTO=porcentaje;
            CODIGO_DESCUENTO=codigo;
            btnCodigoDescuento.setText(""+codigo+" "+porcentaje+"% descuento en envío");
        }else{
            DESCUENTO=0;
            CODIGO_DESCUENTO="";
            btnCodigoDescuento.setText("Agregar código de descuento");
            _ShowAlert("",""+Error,0);
        }
        calculaTotal();
    }



}
