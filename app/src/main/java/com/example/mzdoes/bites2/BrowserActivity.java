package com.example.mzdoes.bites2;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class BrowserActivity extends AppCompatActivity {

    // ANDROID WIDGETS AND TOOLS
    private FloatingActionButton closeButton;
    private ImageButton          navBack, navForward;
    private TextView             addressBar;
    private WebView              webView;

    private String               currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        Intent i = getIntent();
        currentUrl = i.getStringExtra(KeySettings.URL_KEY);

        addressBar = findViewById(R.id.textView_browserAddress);
        addressBar.setText(currentUrl);

        webView = findViewById(R.id.webView_browser);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                addressBar.setText(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        closeButton = findViewById(R.id.floatingActionButton_browserClose);
        navBack     = findViewById(R.id.imageButton_browserNavBack);
        navForward  = findViewById(R.id.imageButton_browserNavForward);
        setButtons();



        webView.loadUrl(currentUrl);
    }

    private void setButtons() {

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) { webView.goBack(); }
                else { Toast.makeText(BrowserActivity.this, "Cannot go back.", Toast.LENGTH_SHORT).show(); }
            }
        });

        navForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) { webView.goForward(); }
                else { Toast.makeText(BrowserActivity.this, "Cannot go forward.", Toast.LENGTH_SHORT).show(); }
            }
        });
    }
}
