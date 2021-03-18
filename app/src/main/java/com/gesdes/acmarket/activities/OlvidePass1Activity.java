package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

public class OlvidePass1Activity extends AppCompatActivity {
    EditText etTelefono;
    Button btnEnviaCodigo;
    String telefono;
    String URL_API;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvide_pass1);
        progressBar=findViewById(R.id.pbCargandoOlvidePas1);
        etTelefono=findViewById(R.id.etCodigoRecupera1);
        btnEnviaCodigo=findViewById(R.id.btnEnviaCodigoolvide1);
        URL_API=getString(R.string.URL_HOST) +"SendSMSPolar";
    }

    public void btnenviaSMS(View view){
        telefono =etTelefono.getText().toString();
        enviaMensaje();
    }

    public void enviaMensaje(){


        if (!validate()) {
            return;
        }

        btnEnviaCodigo.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("TELEFONO", telefono);
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
                                JSONObject cli=response.getJSONObject("cliente");
                                ClienteModel cliente =new ClienteModel();
                                cliente.CODIGO= cli.getString("codigo");
                                cliente.PK= cli.getString("pk");
                                cliente.TELEFONO= cli.getString("telefono");
                                Bundle bolsa=new Bundle();
                                bolsa.putSerializable("clienteNuevo", cliente);
                                Intent main=new Intent(getApplicationContext(), OlvidePass2Activity.class);
                                main.putExtra("bolsa",bolsa);
                                startActivity(main);

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
        if(telefono.isEmpty()){
            _ShowAlert("Error","¡Telefono no puede estar vacío!");
            return false;
        }

        return true;
    }

    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(OlvidePass1Activity.this).create();
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
