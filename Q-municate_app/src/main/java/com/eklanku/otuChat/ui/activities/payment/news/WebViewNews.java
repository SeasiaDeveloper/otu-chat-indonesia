package com.eklanku.otuChat.ui.activities.payment.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eklanku.otuChat.R;

public class WebViewNews extends AppCompatActivity {

    WebView wv;
    String url = "https://eklankumax.com/Berita";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_webview);
        wv = findViewById(R.id.contentWebView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(url);
    }

    private class browser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
