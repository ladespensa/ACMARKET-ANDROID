package com.gesdes.acmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.adapters.DireccionesAdapter;
import com.gesdes.acmarket.model.DireccionesModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiposTiendasModel;
import com.gesdes.acmarket.utils.Utils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.gms.tasks.Tasks.await;

public class DireccionesActivity extends AppCompatActivity implements TextWatcher {

    List<DireccionesModel>DIRECCIONES_LIST;
    List<DireccionesModel>DIRECCIONES_LIST0;
    DireccionesAdapter adapterdir;
    String URL_API,pk_cliente;
    TextView tvBuscar;
    ListView lvLista;
    PlacesClient placesClient;
    String API_PLACES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direcciones);
        URL_API=getString(R.string.URL_HOST)+"ObtenerDireccionesByPkCliente";
        SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        pk_cliente=preferencias.getString("PK","");
        tvBuscar=findViewById(R.id.etBuscaDireccionDireccionesEntrega);
        lvLista=findViewById(R.id.lvDireccionesLista);
        API_PLACES=getString(R.string.google_places);

        tvBuscar.addTextChangedListener(this);

        DIRECCIONES_LIST=new ArrayList<>();
        DIRECCIONES_LIST0=new ArrayList<>();

        DireccionesModel aux=new DireccionesModel();
        aux.DIRECCION="Mi ubicaciòn";
        aux.IMAGEN="gps";
        DIRECCIONES_LIST.add(aux);
        adapterdir= new DireccionesAdapter(DIRECCIONES_LIST,this);
        lvLista.setAdapter(adapterdir);

        obtieneDirecciones();

        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DireccionesModel dir= DIRECCIONES_LIST.get(i);
                if(dir.PLACE_ID!=null && !dir.PLACE_ID.isEmpty()) {
                    new getlatLngFromPlaceId(getApplicationContext(), pg, dir).execute(dir.PLACE_ID);
                }else if(dir.LAT_LNG!=null || dir.DIRECCION.equals("Mi ubicaciòn")){
                    JSONObject bolsa=new JSONObject();
                    if(dir.LAT_LNG!=null){
                        try {
                            bolsa.put("LATITUD",dir.LAT_LNG.latitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            bolsa.put("LONGITUD",dir.LAT_LNG.longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                try {
                    bolsa.put("DIRECCION",dir.DIRECCION);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                data.setData(Uri.parse(bolsa.toString()));
                setResult(RESULT_OK, data);
                finish();
            }
            }
        });

    }

    public void obtieneDirecciones(){

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo direcciones...");
        progressDialog.show();

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_CLIENTE", pk_cliente);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressDialog!=null) {
                                progressDialog.dismiss();
                            }

                            int result = (int) response.get("resultado");

                            if(result == 1){

                                DIRECCIONES_LIST.clear();
                                DIRECCIONES_LIST0.clear();
                                JSONArray dires=response.getJSONArray("direcciones");
                                DireccionesModel aux=new DireccionesModel();
                                aux.DIRECCION="Mi ubicaciòn";
                                aux.IMAGEN="gps";
                                DIRECCIONES_LIST.add(aux);
                                DIRECCIONES_LIST0.add(aux);

                                for (int i=0;i<dires.length();i++) {
                                    JSONObject dire= dires.getJSONObject(i);
                                    aux=new DireccionesModel();
                                    aux.PK=dire.getInt("pk");
                                    aux.DIRECCION=dire.getString("direccion");
                                    aux.IMAGEN="dir";
                                    Double lat =dire.getDouble("latitud");
                                    Double lon=dire.getDouble("longitud");
                                    aux.LAT_LNG=new LatLng(lat,lon);
                                    DIRECCIONES_LIST.add(aux);
                                    DIRECCIONES_LIST0.add(aux);
                                }

                                adapterdir.notifyDataSetChanged();

                            }else{

                                String error=response.getString("mensaje");
                                //_ShowAlert("Error",error);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
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

    public class getDireccionBackFromTv extends AsyncTask<String, String, List<DireccionesModel> > {
        Context mContext;
        ProgressDialog progressDialog;
        TextView tvDireccion;
        String dir = "";
        List<DireccionesModel> lista;

        public getDireccionBackFromTv(Context mContext, ProgressDialog progressDialog, TextView tvDireccion) {
            this.mContext = mContext;
            this.progressDialog = progressDialog;
            this.tvDireccion = tvDireccion;
            lista=new ArrayList<>();
            dir=tvDireccion.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*
            progressDialog = new ProgressDialog(mContext,
                    R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Obteniendo direcciónes...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();*/
        }

        @Override
        protected List<DireccionesModel>  doInBackground(String... params) {

            String lo = params[0];

            lista = Utils.getPrediccion(mContext,lo,API_PLACES);
            return lista;
        }

        @Override
        protected void onPostExecute(List<DireccionesModel> direcciones) {
            super.onPostExecute(direcciones);
            /*if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/
            //tvDireccion.setText(direcciones);
            if(dir.equals(tvDireccion.getText().toString())){
                DIRECCIONES_LIST.clear();
                DIRECCIONES_LIST.addAll(lista);
                adapterdir.notifyDataSetChanged();
            }else if(tvDireccion.getText().length()==0){
                DIRECCIONES_LIST.clear();
                DIRECCIONES_LIST.addAll(DIRECCIONES_LIST0);
                adapterdir.notifyDataSetChanged();
            }

        }
    }

    public class getlatLngFromPlaceId extends AsyncTask<String, String, DireccionesModel > {
        Context mContext;
        ProgressDialog progressDialog;
        DireccionesModel DIR;

        public getlatLngFromPlaceId(Context mContext, ProgressDialog progressDialog, DireccionesModel tvDireccion) {
            this.mContext = mContext;
            this.progressDialog = progressDialog;
            this.DIR = tvDireccion;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*
            progressDialog = new ProgressDialog(mContext,
                    R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Obteniendo direcciónes...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();*/
        }

        @Override
        protected DireccionesModel  doInBackground(String... params) {

            String lo = params[0];

            DireccionesModel lis= Utils.getLatLngFromPlaceId(getApplicationContext(),DIR.PLACE_ID,API_PLACES);
            DIR.LAT_LNG=lis.LAT_LNG;
            return lis;
        }

        @Override
        protected void onPostExecute(DireccionesModel direcciones) {
            super.onPostExecute(direcciones);
            terminaObtenerDatos(DIR);

        }
    }

    ProgressDialog pg;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.length()==0){
            DIRECCIONES_LIST.clear();
            DIRECCIONES_LIST.addAll(DIRECCIONES_LIST0);
            adapterdir.notifyDataSetChanged();
        }else{
            new getDireccionBackFromTv(this,pg,tvBuscar).execute(charSequence.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        int i= 0;
        i+=2;
    }

    public void terminaObtenerDatos(DireccionesModel dir){
        JSONObject bolsa=new JSONObject();
        if(dir!=null){
            if(dir.LAT_LNG!=null){
                try {
                    bolsa.put("LATITUD",dir.LAT_LNG.latitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    bolsa.put("LONGITUD",dir.LAT_LNG.longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            bolsa.put("DIRECCION",dir.DIRECCION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent data = new Intent();
        data.setData(Uri.parse(bolsa.toString()));
        setResult(RESULT_OK, data);
        finish();

    }


}
