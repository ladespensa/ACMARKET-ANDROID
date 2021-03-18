package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.ClienteModel;
import com.gesdes.acmarket.utils.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro4Activity extends AppCompatActivity {

    ClienteModel cliente;
    Spinner spinner;
    EditText etNombre,etApellidos,etFecha,etCorreo;
    TextView etGenero;
    Button btnGuardar;
    String URL_API;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro4);

        URL_API=getString(R.string.URL_HOST)+"completaInformacion";
        progressBar=findViewById(R.id.pbCargandoRegistro4);
        etNombre=findViewById(R.id.etNombreRegistro4);
        etApellidos=findViewById(R.id.etApellidosRegistro4);
        etFecha=findViewById(R.id.etFechaNacimientoRegistro4);
        etCorreo=findViewById(R.id.etEmailRegistro4);
        btnGuardar=findViewById(R.id.btnGuardarRegistro4);

        spinner = (Spinner) findViewById(R.id.spGeneroRegistro4);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.generos, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        cliente = (ClienteModel)bolsa.getSerializable("NuevoUsuario");
    }

    public void registrar(View view){
        cliente.NOMBRE=etNombre.getText().toString();
        cliente.APELLIDOS=etApellidos.getText().toString();
        cliente.FECHA_NACIMIENTO=etFecha.getText().toString();
        cliente.GENERO=spinner.getSelectedItem().toString();
        cliente.CORREO=etCorreo.getText().toString();
        registraUsuario();

    }

    public void registraUsuario() {

        if (!validate()) {
            return;
        }

        btnGuardar.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK", cliente.PK);
            datos.put("TELEFONO", cliente.TELEFONO);
            datos.put("PASSWORD", cliente.PASSWORD);
            datos.put("NOMBRE", cliente.NOMBRE);
            datos.put("APELLIDOS", cliente.APELLIDOS);
            datos.put("GENERO", cliente.GENERO);
            datos.put("FECHA_NACIMIENTO", cliente.FECHA_NACIMIENTO);
            datos.put("CORREO", cliente.CORREO);
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
                            btnGuardar.setEnabled(true);

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
                                cliente.OPENID=cli.get("openid").toString();

                                SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
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

                                _ShowAlert("Registro","Cliente registrado",1);
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
                        btnGuardar.setEnabled(true);

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
        if(cliente.NOMBRE.isEmpty()){
            _ShowAlert("Error","¡Nombre no puede estar vacío!",0);
            return false;
        }else if(cliente.APELLIDOS.isEmpty()){
            _ShowAlert("Error","¡Apellidos no puede estar vacío!",0);
            return false;
        }else if(cliente.FECHA_NACIMIENTO.isEmpty()){
            _ShowAlert("Error","¡Fecha de nacimiento no puede estar vacío!",0);
            return false;
        }else if(cliente.GENERO.isEmpty()){
            _ShowAlert("Error","¡Genero no puede estar vacío!",0);
            return false;
        }else if(cliente.CORREO.isEmpty()){
            _ShowAlert("Error","¡Correo no puede estar vacío!",0);
            return false;
        }

        return true;
    }

    private void _ShowAlert(String title, String mensaje, final int opc){

        AlertDialog alertDialog = new AlertDialog.Builder(Registro4Activity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(opc==1){

                            Intent main=new Intent(getApplicationContext(), MainActivity.class);
                            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(main);
                            finish();
                        }

                    }
                });
        alertDialog.show();
    }

    public void btnCreaCuenta(View view){
        Intent intento = new Intent(getApplicationContext(),Registro1Activity.class);
        startActivity(intento);
    }

    public void calendario(View view){
        showDatePickerDialog();
    }

    private void showDatePickerDialog() {

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = year + "-" + (month + 1) + "-" + day;
                etFecha.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");

    }
}
