package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.gesdes.acmarket.R;

public class TutorialActivity extends AppCompatActivity {

    WebView wvTutorial;
    ImageButton ibCerrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        String id=getIntent().getStringExtra("id");

        wvTutorial=findViewById(R.id.wvTutorial);
        ibCerrar=findViewById(R.id.btnCerrarTutorial);

        ibCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String url = getString(R.string.URL_TUTORIAL)+"?id="+id;
        final WebSettings ajustesVisorWeb = wvTutorial.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        wvTutorial.loadUrl(url);
        wvTutorial.setWebViewClient(new WebViewClient());

    }
}
