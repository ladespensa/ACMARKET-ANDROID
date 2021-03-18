package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.ClienteModel;

public class Registro2Activity extends AppCompatActivity {

    EditText etCodigo;
    String codigo;
    ClienteModel cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        etCodigo=findViewById(R.id.etCodigoRegistro2);
        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        cliente = (ClienteModel)bolsa.getSerializable("NuevoUsuario");

    }

    public void btnValidaCodigo(View view){
        codigo=etCodigo.getText().toString();
        if(codigo.equals(cliente.CODIGO)){
            Bundle bolsa=new Bundle();
            bolsa.putSerializable("NuevoUsuario", cliente);
            Intent intento=new Intent(getApplicationContext(),Registro3Activity.class);
            intento.putExtra("bolsa",bolsa);
            startActivity(intento);
        }else{
            _ShowAlert("Error","¡código no es válido verifiquelo e intente nuevamente!");
        }
    }

    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(Registro2Activity.this).create();
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
