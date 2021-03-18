package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class Registro3Activity extends AppCompatActivity {

    EditText pass1,pass2;
    String password1,password2;
    Button btnIngresar;
    ClienteModel cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro3);
        pass1=findViewById(R.id.etPasswordRegistro3);
        pass2=findViewById(R.id.etConfirmaPasswordRegistro3);
        btnIngresar=findViewById(R.id.btnIngresarRegistro3);
        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        cliente = (ClienteModel)bolsa.getSerializable("NuevoUsuario");

    }

    public void continuar(View view){
        password1=pass1.getText().toString();
        password2=pass2.getText().toString();
        guardaContraseña();
    }

    public void guardaContraseña(){


        if (!validate()) {
            return;
        }else{
            cliente.PASSWORD=password1;
            Bundle bolsa=new Bundle();
            bolsa.putSerializable("NuevoUsuario", cliente);
            Intent intento=new Intent(getApplicationContext(),Registro4Activity.class);
            intento.putExtra("bolsa",bolsa);
            startActivity(intento);
        }



    }

    public boolean validate(){
        if(password1.isEmpty()){
            _ShowAlert("Error","¡Contraseña no puede estar vacío!");
            return false;
        }else if(password2.isEmpty()){
            _ShowAlert("Error","¡Confirmacion de contraseña no puede estar vacío!");
            return false;
        }else if(!password1.equals(password2)){
            _ShowAlert("Error","¡La contraseña debe de ser igual en los dos campos!");
            return false;
        }

        return true;
    }

    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(Registro3Activity.this).create();
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
