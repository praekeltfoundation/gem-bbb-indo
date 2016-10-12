package com.rr.rgem.gem;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.Utils;

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
        webView = (WebView) findViewById(R.id.tipWebView);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("url");
        webView.loadUrl(url);
    }
}
