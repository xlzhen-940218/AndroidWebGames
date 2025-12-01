package com.xlzhen.webgames;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.setWebViewClient(new WebViewClient(){
            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            // 拦截 JavaScript 的 alert() 弹窗
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // 使用原生的 AlertDialog 来显示弹窗内容
                new AlertDialog.Builder(view.getContext())
                        .setTitle("提示")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            // 必须调用 result.confirm() 才能继续执行 JS
                            result.confirm();
                        })
                        .setCancelable(false)
                        .show();

                // 返回 true 表示我们已经处理了弹窗，WebView 不会再做默认处理
                return true;
            }

            // 拦截 JavaScript 的 confirm() 弹窗 (需要处理结果是确定还是取消)
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("确认")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            result.confirm(); // 确认
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            result.cancel(); // 取消
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            // 拦截 JavaScript 的 prompt() 弹窗 (需要处理用户输入)
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                // prompt() 的处理相对复杂，通常也使用 AlertDialog，
                // 并在其中添加 EditText 来获取用户输入，然后调用 result.confirm(userInput)。
                // 为了简化，这里仅作方法提示。
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        String url = getIntent().getStringExtra("url");
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}