package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gesdes.acmarket.R;

public class SplashActivity extends AppCompatActivity {

    String CHANNEL_ID;
    String CHANNEL_DESCRIPTION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        final boolean LOGUEADO= preferencias.getBoolean("LOGUEADO", false);



        //final Intent myintent =new Intent(this, LoginActivity.class);
        CHANNEL_DESCRIPTION=getString(R.string.channel_description);
        CHANNEL_ID= getString(R.string.CHANNEL_ID);
        createNotificationChannel();


        ViewGroup cl=findViewById(R.id.clSplash);
        ImageView iv=findViewById(R.id.ivLogoSplash);
        //enviaNotificacion();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent myintent;
                if(LOGUEADO) {
                    myintent =new Intent(getApplicationContext(), MainActivity.class);
                }else{
                    myintent =new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(myintent);
                finish();
            }
        },2000);


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void enviaNotificacion(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pedido registrado")
                .setContentText("Pedido se entregarà pròximo ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }


}
