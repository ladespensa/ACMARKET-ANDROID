package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.ClienteModel;

import org.json.JSONException;
import org.json.JSONObject;

public class OlvidePass2Activity extends AppCompatActivity {

    EditText etPassword1,etPassword2,etCodigo;
    Button btnEnviaCodigo;
    String codigo,pass1,pass2,_CODIGO,_PK;
    String URL_API;
    ClienteModel cliente;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvide_pass2);

        Bundle bolsa=getIntent().getBundleExtra("bolsa");
        cliente=(ClienteModel)bolsa.getSerializable("clienteNuevo");
        _CODIGO=cliente.CODIGO;
        _PK=cliente.PK;

        progressBar=findViewById(R.id.pbCargandoOlvidePass2);
        etPassword1=findViewById(R.id.etPassword1Recupera);
        etPassword2=findViewById(R.id.etPassword1Recupera2);
        etCodigo=findViewById(R.id.etCodigoRecupera);
        btnEnviaCodigo=findViewById(R.id.btnGuardarOlvide2);
        URL_API=getString(R.string.URL_HOST) +"CambiaPasswordCliente";

    }


    public void btnenviaSMS(View view){
        codigo =etCodigo.getText().toString();
        pass1 =etPassword1.getText().toString();
        pass2 =etPassword2.getText().toString();
        nuevoPass();
    }

    public void nuevoPass(){


        if (!validate()) {
            return;
        }

        btnEnviaCodigo.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK", _PK);
            datos.put("PASSWORD", pass1);
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
                                _ShowAlert("Bien","Contraseña actualizada por favor ingresa con tu nueva contraseña",1);
                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("Error",error,0);

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
                        btnEnviaCodigo.setEnabled(true);

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

    public boolean validate(){
        if(codigo.isEmpty()){
            etCodigo.setError("¡Còdigo no puede estar vacío!");
            return false;
        }if(pass1.isEmpty()){
            etPassword1.setError("¡La contraseña nueva no puede estar vacía!");
            return false;
        }if(pass2.isEmpty()){
            etPassword2.setError("¡La confirmación de contraseña nueva no puede estar vacía!");
            return false;
        }
        if(!codigo.equals(_CODIGO)){
            etCodigo.setError("¡Còdigo no coincide!");
            return false;
        }
        if(!pass2.equals(pass1)){
            etPassword2.setError("¡La confirmación de contraseña nueva no coincide con la contraseña!");
            return false;
        }

        return true;
    }

    private void _ShowAlert(String title, String mensaje, final int opc){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(opc==1){
                            Intent main=new Intent(getApplicationContext(), LoginActivity.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(main);
                            finish();
                        }
                    }
                });
        alertDialog.show();
    }


}
