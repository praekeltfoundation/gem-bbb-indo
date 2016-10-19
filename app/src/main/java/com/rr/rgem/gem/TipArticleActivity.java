package com.rr.rgem.gem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.Utils;

import java.io.IOException;

public class TipArticleActivity extends ApplicationActivity {

    private GEMNavigation navigation;
    private ViewGroup articleScreen;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.toast(this, "starting Tip Article activity");
        navigation = new GEMNavigation(this);
        articleScreen = (ViewGroup) navigation.addLayout(R.layout.tip_article);

        final Persisted persisted = new Persisted(this);
        final View tipProgress = findViewById(R.id.tipProgress);
        //tipProgress.setVisibility(View.GONE);
        webView = (WebView) findViewById(R.id.tipWebView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                tipProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                tipProgress.setVisibility(View.GONE);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // TODO: Token Auth in WebView
                /*
                try {

                } catch (IOException e) {
                    // Tell Client to retry
                    return null;
                }*/

                return super.shouldInterceptRequest(view, request);
            }
        });

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("url");
        webView.loadUrl(url);
    }
}
