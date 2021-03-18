package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gesdes.acmarket.R;

public class PedidoRealizadoActivity extends AppCompatActivity {

    String CHANNEL_ID;
    String DIA_ENTREGA;
    TextView tvTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_realizado);

        tvTexto=findViewById(R.id.tvPedidollegara);
        Bundle bolsa= getIntent().getBundleExtra("bolsa");
        DIA_ENTREGA=bolsa.getString("DIA_ENTREGA");

        tvTexto.setText("Tù pedido llegarà "+DIA_ENTREGA);

        CHANNEL_ID= getString(R.string.CHANNEL_ID);
        createNotificationChannel();

        //configuracion primer pedido
        //revisamos si ya realice mi primer pedido actualizo la cifra para que al volver a pedir sin salir de la aplicacion
        //ya no me considere el descuento de primer pedido
        SharedPreferences preferencias =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        int pedidos=preferencias.getInt("CANTIDAD_MIS_PEDIDOS",0);
        if(pedidos==0){
            pedidos=1;
            editor.putInt("CANTIDAD_MIS_PEDIDOS",pedidos);
            editor.commit();
        }
        //termina configuracion de primer pedido

    }

    public void finaliza(View view){
        finalizar();
    }

    public void finalizar(){
        Intent intento = new Intent(this,MainActivity.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intento);
        enviaNotificacion();
        finish();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void enviaNotificacion(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Pedido registrado")
                .setContentText("Pedido se entregarà el pròximo "+DIA_ENTREGA)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Pedido se entregarà el pròximo "+DIA_ENTREGA));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }
    @Override
    public void onBackPressed() {
        finalizar();
        //_ShowAlertOption();
        //super.onBackPressed();

    }


}
