package com.xlzhen.webgames;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "https://webgames.codetools.fun/";
    private static final String LOCAL_URL = "file:///android_asset/webgames/index.html";
    private WebView webView;
    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = NetworkUtils.isNetworkConnected(this)?URL:LOCAL_URL;
        // 设置全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        homeButton = findViewById(R.id.homeButton);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(request.getUrl().toString().endsWith("index.html") || request.getUrl().toString().equals("https://webgames.codetools.fun/")) {
                    return super.shouldOverrideUrlLoading(view, request);
                }else{
                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("url", request.getUrl().toString());
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.endsWith("index.html")|| url.equals("https://webgames.codetools.fun/")) {
                    homeButton.setVisibility(View.GONE);
                } else {
                    homeButton.setVisibility(View.VISIBLE);
                }
            }
        });

        homeButton.setOnClickListener(v -> webView.loadUrl(url));

        webView.loadUrl(url);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }
}