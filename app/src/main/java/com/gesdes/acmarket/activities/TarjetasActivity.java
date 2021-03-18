package com.gesdes.acmarket.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.adapters.TarjetasAdapter;
import com.gesdes.acmarket.model.TarjetasModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TarjetasActivity extends AppCompatActivity {

    List<TarjetasModel>TARJETAS_LIST;
    TarjetasAdapter tarjetasAdapter;
    ListView lvTarjetas;
    String URL_API,pkCliente;
    ProgressBar progressBar;
    Button btnAgregar;
    TextView tvAlertaMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjetas);


        URL_API=getString(R.string.URL_HOST)+"ListaTarjetasClientesByPkCliente";
        SharedPreferences preferences =getSharedPreferences("VARIABLES",MODE_PRIVATE);
        pkCliente= preferences.getString("PK",null);
        TARJETAS_LIST=new ArrayList<>();
        tarjetasAdapter=new TarjetasAdapter(TARJETAS_LIST,this);
        progressBar=findViewById(R.id.pbCargandoTarjetasList);
        lvTarjetas=findViewById(R.id.lvTarjetasList);
        lvTarjetas.setAdapter(tarjetasAdapter);
        btnAgregar=findViewById(R.id.btnAgregarTarjeta);
        tvAlertaMensaje=findViewById(R.id.tvAlertaOpcioinesPagotv);

        boolean agregar=preferences.getBoolean("PRODUCCION_OPEN_PAY",false);
        //if(!agregar){
        //    btnAgregar.setVisibility(View.GONE);
        //}

        tvAlertaMensaje.setText(Html.fromHtml("Te sugerimos <b>pagar en lìnea</b> para mantener una sana distancia. Tu bienestar es lo màs importante."));

        lvTarjetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TarjetasModel tarjeta=(TarjetasModel) adapterView.getItemAtPosition(i);
                JSONObject bolsa=new JSONObject();
                try {
                    bolsa.put("ID", tarjeta.ID);
                    bolsa.put("BANK_NAME", tarjeta.BANK_NAME);
                    bolsa.put("BRAND", tarjeta.BRAND);
                    bolsa.put("CARD_NUMBRE", tarjeta.CARD_NUMBRE);
                }catch (Exception e){

                }
                Intent intent=new Intent();
                intent.setData(Uri.parse(bolsa.toString()));
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //obtenerTarjetas();
    }

    public void obtenerTarjetas(){

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos=new JSONObject();
        try{
            datos.put("PK_CLIENTE",pkCliente);
        }catch (Exception e){}

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
                                JSONArray tarjetas=response.getJSONArray("tarjetas");
                                TarjetasModel aux;
                                TARJETAS_LIST.clear();
                                aux=new TarjetasModel();
                                aux.ID="EFECTIVO";
                                aux.BANK_NAME="EFECTIVO";
                                aux.BRAND="EFECTIVO";
                                aux.CARD_NUMBRE="EFECTIVO";
                                TARJETAS_LIST.add(aux);
                                aux=new TarjetasModel();
                                aux.ID="TERMINAL";
                                aux.BANK_NAME="TERMINAL";
                                aux.BRAND="TERMINAL";
                                aux.CARD_NUMBRE="TERMINAL";
                                TARJETAS_LIST.add(aux);
                                for (int i=0;i<tarjetas.length();i++) {
                                    JSONObject tarjeta= tarjetas.getJSONObject(i);
                                    aux=new TarjetasModel();
                                    aux.ID=tarjeta.getString("id");
                                    aux.BANK_NAME=tarjeta.getString("bankName");
                                    aux.BRAND=tarjeta.getString("brand");
                                    aux.CARD_NUMBRE=tarjeta.getString("cardNumber");
                                    TARJETAS_LIST.add(aux);
                                }
                                if(tarjetas.length()<3){
                                    btnAgregar.setVisibility(View.VISIBLE);
                                }else{
                                    btnAgregar.setVisibility(View.GONE);
                                }
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
                        if(progressBar!=null){
                            progressBar.setVisibility(View.GONE);
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

    public void agregarTarjeta(View view){
        Intent intento=new Intent(TarjetasActivity.this,AgregarTarjetaActivity.class);
        startActivity(intento);
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerTarjetas();
    }

}
