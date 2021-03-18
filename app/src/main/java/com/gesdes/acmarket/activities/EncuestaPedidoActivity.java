package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.gesdes.acmarket.R;

public class EncuestaPedidoActivity extends AppCompatActivity {

    WebView wvEncuesta;
    ImageButton ibCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_pedido);


        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        String PK=preferences.getString("PK","");

        Bundle bolsa =getIntent().getBundleExtra("bolsa");
        int PK_PEDIDO=bolsa.getInt("PK_PEDIDO");
        wvEncuesta=findViewById(R.id.wvEncuesta);
        ibCerrar=findViewById(R.id.btnCerrarEncuesta);

        ibCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String url = getString(R.string.URL_ENCUESTA)+"id_user="+PK+"&id_pedido="+PK_PEDIDO;
        final WebSettings ajustesVisorWeb = wvEncuesta.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        wvEncuesta.loadUrl(url);

// Forzamos el webview para que abra los enlaces internos dentro de la la APP
        wvEncuesta.setWebViewClient(new WebViewClient());
// Forzamos el webview para que abra los enlaces externos en el navegador
        //wvEncuesta.setWebViewClient(new MyAppWebViewClient());


    }
}
