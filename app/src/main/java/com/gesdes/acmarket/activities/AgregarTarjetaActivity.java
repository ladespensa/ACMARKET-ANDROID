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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.CategoriasModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.prefs.Preferences;

import mx.openpay.android.Openpay;
import mx.openpay.android.OperationCallBack;
import mx.openpay.android.OperationResult;
import mx.openpay.android.exceptions.OpenpayServiceException;
import mx.openpay.android.exceptions.ServiceUnavailableException;
import mx.openpay.android.model.Card;
import mx.openpay.android.model.Token;
import mx.openpay.android.validation.CardValidator;

public class AgregarTarjetaActivity extends AppCompatActivity  implements OperationCallBack {

    EditText etTitular,etNumeroTarjeta,etCCV;
    Spinner spAño,spMes;
    List<String>listaAños;
    List<String>listaMeses;
    ArrayAdapter<String> mesesAdapter;
    ArrayAdapter<String> añosAdapter;
    String ID_TARJETA,URL_API,deviceIdString,PK_CLIENTE;
    Boolean produccion=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarjeta);

        URL_API=getString(R.string.URL_HOST)+"AgregaTarjetasClientes";
        //charid= getString(R.string.merchantId);
        //produccion=Boolean.parseBoolean(getString(R.string.OPEN_PRODUCCION));

        SharedPreferences preferences =getSharedPreferences("VARIABLES",Context.MODE_PRIVATE);
        PK_CLIENTE=preferences.getString("PK","");
        apiopenpay= preferences.getString("PUBLIC_OPEN_KEY","");
        produccion=preferences.getBoolean("PRODUCCION_OPEN_PAY",false);
        charid=preferences.getString("ID_OPEN","");

        etTitular=findViewById(R.id.etTitular);
        etNumeroTarjeta=findViewById(R.id.etNumeroTarjeta);
        etCCV=findViewById(R.id.etCCV);
        spAño=findViewById(R.id.spAño);
        spMes=findViewById(R.id.spMes);

        listaAños=new ArrayList<>();
        Calendar calendario=Calendar.getInstance();
        int year = calendario.get(Calendar.YEAR);
        listaAños.add("Año");
        for(int i=0; i<21;i++){
            int opc=year+i;
            listaAños.add(""+String.valueOf(opc).substring(2));
        }



        //Collections.addAll(listaAños);
        añosAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, listaAños);
        //mesesAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, listaMeses);
        spAño.setAdapter(añosAdapter);
        //spMes.setAdapter(mesesAdapter);


    }

    public void guardar(View view){
        guardarTarjeta();
    }
    String charid,apiopenpay;
    public void guardarTarjeta(){

        String Titular=etTitular.getText().toString();
        String numerotarjeta=etNumeroTarjeta.getText().toString();
        String cvv=etCCV.getText().toString();
        int año= getInteger(spAño.getSelectedItem().toString());
        int mes= getInteger(spMes.getSelectedItem().toString());

        com.gesdes.acmarket.model.OpenPayApp open=new com.gesdes.acmarket.model.OpenPayApp(charid,apiopenpay,produccion);
        Openpay openpay =open.getOpenpay();

        deviceIdString = openpay.getDeviceCollectorDefaultImpl().setup(this);
            /*if (deviceIdString == null) {
                this.printMsg(openpay.getDeviceCollectorDefaultImpl().getErrorMessage());
            } else {
                this.printMsg(deviceIdString);
            }*/
        Card card = new Card();
        boolean isValid = true;

        card.holderName(Titular);
        if (!CardValidator.validateHolderName(Titular)) {
            etTitular.setError("Titular es requerido");
            isValid = false;
        }
        card.cardNumber(numerotarjeta);
        if (!CardValidator.validateNumber(numerotarjeta)) {
            etNumeroTarjeta.setError("Número inválido");
            isValid = false;
        }

        card.cvv2(cvv);
        if (!CardValidator.validateCVV(cvv, numerotarjeta)) {
            etCCV.setError("Código de seguridad inválido");
            isValid = false;
        }
        if (!CardValidator.validateExpiryDate(mes, año)) {
            _ShowAlert("Error","Fecha de expiracion inválida",0);
            isValid = false;
        }

        card.expirationMonth(mes);
        card.expirationYear(año);

        if (isValid) {

            progressDialog = new ProgressDialog(this,
                    R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registrando tarjeta...");
            progressDialog.show();

            openpay.createToken(card, this);

            //registrarTarjeta();


        }

    }
    ProgressDialog progressDialog;
    private void _ShowAlert(String title, String mensaje,final int opc){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
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


    @Override
    public void onError(final OpenpayServiceException error) {
        error.printStackTrace();
        progressDialog.dismiss();
        int desc = 0;
        String msg = null;
        switch (error.getErrorCode()) {
            case 3001:
                desc = R.string.declined;
                msg = this.getString(desc);
                break;
            case 3002:
                desc = R.string.expired;
                msg = this.getString(desc);
                break;
            case 3003:
                desc = R.string.insufficient_funds;
                msg = this.getString(desc);
                break;
            case 3004:
                desc = R.string.stolen_card;
                msg = this.getString(desc);
                break;
            case 3005:
                desc = R.string.suspected_fraud;
                msg = this.getString(desc);
                break;

            case 2002:
                desc = R.string.already_exists;
                msg = this.getString(desc);
                break;
            default:
                desc = R.string.error_creating_card;
                msg = error.getDescription();
        }

        _ShowAlert("Error", msg,0);
    }

    @Override
    public void onCommunicationError(final ServiceUnavailableException error) {
        error.printStackTrace();
        progressDialog.dismiss();
        _ShowAlert("Error",this.getString(R.string.communication_error),0);
    }

    @Override
    public void onSuccess(final OperationResult result) {
        progressDialog.dismiss();
        //_ShowAlert(getString(R.string.card_added),getString(R.string.card_created));
        Token token=(Token) result.getResult();
        ID_TARJETA=token.getId();
        registrarTarjeta();
    }



    private void clearData() {
        etTitular.setText("");
        etNumeroTarjeta.setText("");
        etCCV.setText("");
        spAño.setSelection(0);
        spMes.setSelection(0);
    }

    private Integer getInteger(final String number) {
        try {
            return Integer.valueOf(number);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public void registrarTarjeta(){


        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registrando tarjeta...");
        progressDialog.show();

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_CLIENTE", PK_CLIENTE);
            datos.put("ID_TARJETA", ID_TARJETA);
            datos.put("DEVICE_SESSION_ID", deviceIdString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();

                            int result = (int) response.get("resultado");

                            if(result == 1){
                                //finish();
                                clearData();
                                _ShowAlert("Bien","¡Tarjeta registrada!",1);
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



}
