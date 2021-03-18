package com.gesdes.acmarket.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.db.AppDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    String token,pkCliente,URL_API,URL_COMPARTIR,NOMBRE,IMAGEN,APELLIDOS,TELEDONO;
    AppBarConfiguration appBarConfiguration;
    TextView tvNombre,tvTelefono;
    ImageView imFoto;
    DrawerLayout drawerLayoutMain;
    AppDatabase db;

    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL_API=getString(R.string.URL_HOST)+"CambiaToken";

        SharedPreferences preferencias =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        pkCliente=preferencias.getString("PK","");
        NOMBRE=preferencias.getString("NOMBRE","");
        APELLIDOS=preferencias.getString("APELLIDOS","");
        TELEDONO=preferencias.getString("TELEFONO","");
        IMAGEN=preferencias.getString("FOTO","");

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        borraPedidos();

        drawerLayoutMain=findViewById(R.id.drawerLayoutMain);
/*
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_bars);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //NavigationUI.setupWithNavController(toolbar, navController);
*/

        NavController navController = Navigation.findNavController(this, R.id.fragmentMainHost);
        NavigationView navView = findViewById(R.id.leftNavigationMain);
        NavigationUI.setupWithNavController(navView, navController);


        BottomNavigationView navView1 = findViewById(R.id.bottomNavigationViewMain);
        NavigationUI.setupWithNavController(navView1, navController);


        try {
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph())
                            .setDrawerLayout(drawerLayoutMain)
                            .build();
        }catch (Exception e){

        }

        View header = navView.getHeaderView(0);
        tvNombre = (TextView) header.findViewById(R.id.tvNombreNavLeftHeaderNav);
        tvTelefono = (TextView) header.findViewById(R.id.tvTelefonoLeftHeaderNav);
        imFoto = (ImageView) header.findViewById(R.id.ivFotoPerfilLeftHeader);
        tvNombre.setText(NOMBRE);
        tvTelefono.setText(TELEDONO);

        byte[] decodedString = Base64.decode(IMAGEN, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodedByte);
        roundedBitmapDrawable.setCircular(true);
        imFoto.setImageDrawable(roundedBitmapDrawable);

        registraToken();
    }

    public void abreMenu(View view){
        drawerLayoutMain.openDrawer(Gravity.START);
    }
/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid= item.getItemId();
        if(item.getItemId()==16908332){
            drawerLayoutMain.openDrawer(Gravity.START);
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void registraToken(){

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();

                JSONObject datos=new JSONObject();
                try{
                    datos.put("PK",pkCliente);
                    datos.put("TOKEN",deviceToken);
                    datos.put("PLATAFORMA","ANDROID");
                }catch (Exception e){}

                RequestQueue requstQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int re=response.getInt("resultado");
                                }catch (Exception e){
                                    String error=e.toString();

                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {

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
        });
    }

    public void cerrarSesion(View view){
        SharedPreferences preferencias =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("LOGUEADO",false);
        editor.commit();
        Intent main=new Intent(this, LoginActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(main);
        finish();
    }


    @Override
    public void onBackPressed() {
        //finish();

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else super.onBackPressed();


    }


    public void compartir(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = URL_COMPARTIR;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "La despensa app");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartir via"));

    }

    public void borraPedidos(){

        db.PedidoDetalleDao().deleteAllProductos();

    }

}
