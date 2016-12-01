package org.gem.indo.dooit.views.web;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class MinimalWebViewActivity extends DooitActivity {

    private static final String INTENT_URL = "intent_webView_url";
    private static final String INTENT_TITLE = "intent_webView_title";
    private static final String INTENT_NO_CARET = "intent_noCaret_title";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_settings_web_view)
    WebView webView;

    @BindView(R.id.activity_settings_web_progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(getIntent().hasExtra(INTENT_NO_CARET)){
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            else{
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_d_back_caret_pink);
            }

            if (getIntent().hasExtra(INTENT_TITLE))
                actionBar.setTitle(getIntent().getStringExtra(INTENT_TITLE));
            else
                actionBar.setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (Build.VERSION_CODES.JELLY_BEAN_MR2 == Build.VERSION.SDK_INT)
            webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if (!hasInternetConnection()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }

                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        webView.loadUrl(getIntent().getStringExtra(INTENT_URL));
    }

    protected boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static class Builder extends DooitActivityBuilder<Builder> {

        protected Builder(Context context) {
            super(context);
        }

        public static Builder create(Context context) {
            return new Builder(context);
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, MinimalWebViewActivity.class);
        }

        @Override
        protected boolean checkIntentCompleteness() {
            return intent.hasExtra(INTENT_URL);// && intent.hasExtra(INTENT_TITLE);
        }

        public Builder setTitle(String title) {
            intent.putExtra(INTENT_TITLE, title);
            return this;
        }

        public Builder setUrl(String url) {
            intent.putExtra(INTENT_URL, url);
            return this;
        }
        public Builder setNoCaret() {
            intent.putExtra(INTENT_NO_CARET, "caret");
            return this;
        }
    }
}
