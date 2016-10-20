package com.rr.rgem.gem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.views.Utils;

import java.io.IOException;
import java.net.MalformedURLException;

public class TipArticleActivity extends ApplicationActivity {

    private static final String TAG = "TipArticle";

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

        webView = (WebView) findViewById(R.id.tipWebView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                tipProgress.setVisibility(View.VISIBLE);
                Log.d(TAG, "Page started");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                tipProgress.setVisibility(View.GONE);
                Log.d(TAG, "Page finished");
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
                Log.d(TAG, "Should intercept request");
                return super.shouldInterceptRequest(view, request);
            }
        });

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        Bundle extras = getIntent().getExtras();
        String url = extras.getString("url");
        try {
            url = factory.joinUrl(url);
            Log.d(TAG, "Loading " + url);
            webView.loadUrl(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed Article URL", e);
        }
    }
}
