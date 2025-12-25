package com.xlzhen.webgames;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "https://webgames.codetools.fun/";
    private static final String LOCAL_URL = "file:///android_asset/webgames/index.html";
    private static final String PREFS_NAME = "WebGamesPrefs";
    private static final String PREF_MODE = "mode";
    private static final int MODE_AUTO = 2;
    private static final int MODE_ONLINE = 0;
    private static final int MODE_OFFLINE = 1;
    
    private WebView webView;
    private Button homeButton;
    private Button modeButton;
    private int currentMode;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);

        // 初始化SharedPreferences
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentMode = preferences.getInt(PREF_MODE, MODE_AUTO);

        webView = findViewById(R.id.webView);
        homeButton = findViewById(R.id.homeButton);
        modeButton = findViewById(R.id.modeButton);
        
        // 更新模式按钮文本
        updateModeButtonText();
        
        // 获取当前URL
        String url = getCurrentUrl();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
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

            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
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
        homeButton.setOnClickListener(v -> webView.loadUrl(getCurrentUrl()));
        
        modeButton.setOnClickListener(v -> showModeSelectionDialog());

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
    
    /**
     * 获取当前模式对应的URL
     */
    private String getCurrentUrl() {
        switch (currentMode) {
            case MODE_ONLINE:
                return URL;
            case MODE_OFFLINE:
                return LOCAL_URL;
            case MODE_AUTO:
            default:
                return NetworkUtils.isNetworkConnected(this) ? URL : LOCAL_URL;
        }
    }
    
    /**
     * 显示模式选择对话框
     */
    private void showModeSelectionDialog() {
        String[] modes = {
                getString(R.string.online_mode) + " - " + getString(R.string.online_mode_desc),
                getString(R.string.offline_mode) + " - " + getString(R.string.offline_mode_desc),
                "自动模式 - 根据网络状态自动选择"
        };
        
        int checkedItem = currentMode;
        
        new AlertDialog.Builder(this)
                .setTitle("选择游戏模式")
                .setSingleChoiceItems(modes, checkedItem, (dialog, which) -> {
                    int newMode = which;
                    if (newMode != currentMode) {
                        currentMode = newMode;
                        preferences.edit().putInt(PREF_MODE, currentMode).apply();
                        updateModeButtonText();
                        
                        // 重新加载页面
                        webView.loadUrl(getCurrentUrl());
                        
                        String modeName = "";
                        switch (currentMode) {
                            case MODE_ONLINE:
                                modeName = getString(R.string.online_mode);
                                break;
                            case MODE_OFFLINE:
                                modeName = getString(R.string.offline_mode);
                                break;
                            case MODE_AUTO:
                                modeName = "自动模式";
                                break;
                        }
                        
                        Toast.makeText(MainActivity.this, 
                                "已切换到" + modeName, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    /**
     * 更新模式按钮文本
     */
    private void updateModeButtonText() {
        String modeText;
        switch (currentMode) {
            case MODE_ONLINE:
                modeText = getString(R.string.online_mode);
                break;
            case MODE_OFFLINE:
                modeText = getString(R.string.offline_mode);
                break;
            case MODE_AUTO:
            default:
                modeText = "自动";
                break;
        }
        modeButton.setText(String.format(getString(R.string.current_mode), modeText));
    }
}
