package com.gesdes.acmarket.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.ProductosModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CodigoDescuentoDialog extends AppCompatDialogFragment {
    EditText etCodigo;
    CodigoDescuentoDialogListener listener;
    Context mContext;
    ProgressBar progressBar;
    String Codigo="",URL_API,PK_CLIENTE;
    Button btnBuscar;
    AlertDialog.Builder builder;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder=new AlertDialog.Builder(getActivity());

        SharedPreferences preferences =mContext.getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        PK_CLIENTE=preferences.getString("PK","");


        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_dialog_codigo_descuento,null);

        builder.setView(view)
                .setTitle("")
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        /*
                .setPositiveButton("buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Codigo=etCodigo.getText().toString();
                        obtenerCodigo();
                    }
                });
*/
        etCodigo=view.findViewById(R.id.etCodigoDescuentoDialog);
        progressBar=view.findViewById(R.id.pbCargandoCodigoDescuentoDialog);
        URL_API=getString(R.string.URL_HOST)+"BuscarCodigoDescuento";
        progressBar.setVisibility(View.GONE);
        btnBuscar=view.findViewById(R.id.btnBuscarCodigoDescuento);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBuscar.setVisibility(View.GONE);
                obtenerCodigo();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        try {
            listener=(CodigoDescuentoDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "tiene que implementar CodigoDescuentoDialogListener");
        }
    }

    public interface CodigoDescuentoDialogListener{
        void cambiaTexto(String codigo,String Error,int resultado,int porcentaje);
    }

    public void obtenerCodigo(){

        progressBar.setVisibility(View.VISIBLE);
        Codigo=etCodigo.getText().toString();

        if(Codigo.trim().isEmpty()){
            etCodigo.setError("Código inválido");
            return;
        }

        JSONObject datos = new JSONObject();
        try {
            datos.put("CODIGO", Codigo);
            datos.put("PK_CLIENTE", PK_CLIENTE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requstQueue = Volley.newRequestQueue(mContext);

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
                                String error=response.getString("mensaje");
                                JSONObject codigoObj=response.getJSONObject("codigo");
                                String CodigoText=codigoObj.getString("codigo");
                                int porcen=codigoObj.getInt("pocentajE_DESCUENTO");
                                listener.cambiaTexto(CodigoText,error,result,porcen);
                            }else{

                                String error=response.getString("mensaje");
                                listener.cambiaTexto("",error,result,0);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        btnBuscar.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(progressBar!=null){
                            progressBar.setVisibility(View.GONE);
                        }
                        btnBuscar.setVisibility(View.VISIBLE);
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

    public void obtenerCodigo1(View view){
        obtenerCodigo();
    }

}
