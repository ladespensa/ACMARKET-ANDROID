package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    EditText etUsuario,etPassword;
    String user,pass;
    Button btnLogin;
    ProgressBar progressBar;
    String URL_API;
    SharedPreferences preferencias;
    SharedPreferences.Editor editor;
    boolean tutorial=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar=findViewById(R.id.pbCargandoLogin);
        etUsuario=findViewById(R.id.etUsuario);
        etPassword=findViewById(R.id.etPasswordLogin);
        btnLogin=findViewById(R.id.btnIngresarLogin);
        URL_API =getString(R.string.URL_HOST)+"ClientesLogin";
        pedirPermisos();
        preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        editor = preferencias.edit();
        tutorial=preferencias.getBoolean("TUTORIAL",false);
        if(!tutorial){
            Intent tuto= new Intent(LoginActivity.this,TutorialActivity.class);
            tuto.putExtra("id","1");
            startActivity(tuto);
            editor.putBoolean("TUTORIAL",true);
            //editor.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void iniciarSesion(View view){
        user=etUsuario.getText().toString();
        pass=etPassword.getText().toString();
        login();
    }

    public void login() {

        if (!validate()) {
            return;
        }

        btnLogin.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("TELEFONO", user);
            datos.put("PASSWORD",pass);
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
                            btnLogin.setEnabled(true);
                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONObject cli=response.getJSONObject("cliente");
                                ClienteModel cliente=new ClienteModel();
                                cliente.PK=cli.get("pk").toString();
                                cliente.TELEFONO=cli.get("telefono").toString();
                                cliente.PASSWORD=cli.get("password").toString();
                                cliente.NOMBRE=cli.get("nombre").toString();
                                cliente.APELLIDOS=cli.get("apellidos").toString();
                                cliente.FECHA_NACIMIENTO=cli.get("fechA_NACIMIENTO").toString();
                                cliente.GENERO=cli.get("genero").toString();
                                cliente.CORREO=cli.get("correo").toString();
                                cliente.CODIGO=cli.get("codigo").toString();
                                cliente.FOTO=cli.get("foto").toString();
                                cliente.BORRADO=cli.get("borrado").toString();
                                cliente.FECHA_C=cli.get("fechA_C").toString();
                                cliente.FECHA_M=cli.get("fechA_M").toString();
                                cliente.FECHA_D=cli.get("fechA_D").toString();
                                cliente.USUARIO_C=cli.get("usuariO_C").toString();
                                cliente.USUARIO_M=cli.get("usuariO_M").toString();
                                cliente.USUARIO_D=cli.get("usuariO_D").toString();


                                editor.putString("PK",cliente.PK);
                                editor.putString("TELEFONO",cliente.TELEFONO);
                                editor.putString("NOMBRE",cliente.NOMBRE);
                                editor.putString("APELLIDOS",cliente.APELLIDOS);
                                editor.putString("FECHA_NACIMIENTO",cliente.FECHA_NACIMIENTO);
                                editor.putString("GENERO",cliente.GENERO);
                                editor.putString("CORREO",cliente.CORREO);
                                editor.putString("OPENID",cliente.OPENID);
                                editor.putBoolean("LOGUEADO",true);
                                editor.putString("FOTO",cliente.FOTO);
                                //editor.putString("TOKEN",cliente.TOKEN);
                                editor.commit();
                                Intent main=new Intent(getApplicationContext(), MainActivity.class);
                                //TODO_SERGIO Intent main=new Intent(getApplicationContext(), TiendasActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(main);
                                finish();

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
                        btnLogin.setEnabled(true);
                        _ShowAlert("Error","¡Ocurrio un error intente màs tarde!");
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
        if(user.isEmpty()){
            _ShowAlert("Error","¡Telefono no puede estar vacío!");
            return false;
        }else if(pass.isEmpty()){
            _ShowAlert("Error","¡Contraseña no puede estar vacío!");
            return false;
        }

        return true;
    }

    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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

    public void btnCreaCuenta(View view){
        Intent intento = new Intent(getApplicationContext(),Registro1Activity.class);
        startActivity(intento);
    }

    int REQUEST_LOCATION=1;
    public void pedirPermisos(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Aquí muestras confirmación explicativa al usuario
                // por si rechazó los permisos anteriormente
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Por favor es necesario aceptar los permisos para usar ACMarket", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void olvideContraseña(View view){

        Intent actiu=new Intent(this,OlvidePass1Activity.class);
        startActivity(actiu);

    }

}
