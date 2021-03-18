package com.gesdes.acmarket.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.LoginActivity;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String Imgbase64;
    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    ProgressBar progressBar;


    EditText etNombre,etApellidos,etEmail,etCelular;
    String Nombre,Apellidos,Email,Celular,URL_API,pkCliente;
    Button btnGuardar;
    MenuItem menuItem,menuItem1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        try {
            BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNavigationViewMain);
            Menu drawer_menu = navigation.getMenu();
            menuItem = drawer_menu.findItem(R.id.navigation_perfil);
        }catch (Exception e){}
        try{
            BottomNavigationView navigation1 = getActivity().findViewById(R.id.bottomNavigationViewMain);
            NavigationView navigationLeft = getActivity().findViewById(R.id.leftNavigationMain);
            Menu drawer_menu1 = navigation1.getMenu();
            menuItem1 = drawer_menu1.findItem(R.id.navigation_perfil);
        }catch (Exception e){}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_perfil, container, false);

        URL_API=getString(R.string.URL_HOST)+"actualizaDatosPerfilCliente";
        btnGuardar=view.findViewById(R.id.btnGuardarPerfilfr);
        etNombre=view.findViewById(R.id.etNombrePerfilfr);
        etApellidos=view.findViewById(R.id.etApellidosPerfilFr);
        etEmail=view.findViewById(R.id.etMailPerfilFr);
        etCelular=view.findViewById(R.id.etCelularPerfilFr);
        mimageView=view.findViewById(R.id.ivFotoPerfil);
        progressBar=view.findViewById(R.id.pbCargandoPerfilFrangment);

        try {
            BottomNavigationView navigation = container.findViewById(R.id.bottomNavigationViewMain);
            Menu drawer_menu = navigation.getMenu();
            menuItem = drawer_menu.findItem(R.id.navigation_perfil);
        }catch (Exception e){}
        try{
            NavigationView navigationLeft = container.findViewById(R.id.leftNavigationMain);
            Menu drawer_menu1 = navigationLeft.getMenu();
            menuItem1 = drawer_menu1.findItem(R.id.navigation_perfil);
        }catch (Exception e){}


        SharedPreferences preferences =getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        pkCliente=preferences.getString("PK","");
        Nombre=preferences.getString("NOMBRE","");
        Apellidos=preferences.getString("APELLIDOS","");
        Email=preferences.getString("CORREO","");
        Celular=preferences.getString("TELEFONO","");
        Imgbase64=preferences.getString("FOTO","");
        if(!Imgbase64.isEmpty()){
            byte[] decodedString = Base64.decode(Imgbase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodedByte);
            roundedBitmapDrawable.setCircular(true);
            mimageView.setImageDrawable(roundedBitmapDrawable);
        }
        mimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
            }
        });

        etNombre.setText(Nombre);
        etApellidos.setText(Apellidos);
        etEmail.setText(Email);
        etCelular.setText(Celular);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardaDatos();
            }
        });

        if(menuItem!=null && !menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }
        if(menuItem1!=null && !menuItem1.isChecked())
        {
            menuItem1.setChecked(true);
        }

        return view;
    }

    public void guardaDatos(){

        progressBar.setVisibility(View.VISIBLE);

        Nombre=etNombre.getText().toString();
        Apellidos=etApellidos.getText().toString();
        Email=etEmail.getText().toString();

        if(!validate()){
            return;
        }

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK",pkCliente);
            datos.put("NOMBRE",Nombre);
            datos.put("APELLIDOS",Apellidos);
            datos.put("CORREO",Email);
            if(!Imgbase64.isEmpty()){
                datos.put("FOTO",Imgbase64);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue requstQueue = Volley.newRequestQueue(getActivity());

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

                                SharedPreferences preferencias = getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("FOTO",Imgbase64);
                                editor.putString("NOMBRE",Nombre);
                                editor.putString("APELLIDOS",Apellidos);
                                editor.putString("CORREO",Email);
                                editor.commit();
                                _ShowAlert("Bien","¡Datos actualizados correctamente!");

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
        if(Nombre.isEmpty()){
            etNombre.setError("El nombre no puede estar vacìo");
            return false;
        }
        if(Apellidos.isEmpty()){
            etApellidos.setError("El campo apellidos no puede estar vacìo");
            return false;
        }
        if(Email.isEmpty()){
            etEmail.setError("El campo email no puede estar vacìo");
            return false;
        }

        return true;
    }


    public void cerrarSesion(View view){
        SharedPreferences preferencias =getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("LOGUEADO",false);
        editor.commit();
        Intent main=new Intent(getContext(), LoginActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(main);
        getActivity().finish();
    }

    public void tomarFoto(){

        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(imageTakeIntent.resolveActivity(getActivity().getPackageManager())!=null){

            startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if(resultCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
            roundedBitmapDrawable.setCircular(true);
            mimageView.setImageDrawable(roundedBitmapDrawable);
            String encodedImage = encodeImage(imageBitmap);
            Imgbase64=encodedImage;

        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100  ,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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

    public void abreMenu(View view){
        DrawerLayout drawerLayoutMain=  getActivity().findViewById(R.id.drawerLayoutMain);
        drawerLayoutMain.openDrawer(Gravity.START);
    }

}
