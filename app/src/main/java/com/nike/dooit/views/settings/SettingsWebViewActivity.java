package com.nike.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.nike.dooit.R;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class SettingsWebViewActivity extends AppCompatActivity {
    private static final String INTENT_URL = "intent_webView_url";
    private static final String INTENT_TITLE = "intent_webView_title";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_settings_web_view)
    WebView webView;
    @BindView(R.id.activity_settings_web_progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        if (getIntent().hasExtra(INTENT_TITLE))
            getSupportActionBar().setTitle(getIntent().getStringExtra(INTENT_TITLE));

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
            return new Intent(context, SettingsWebViewActivity.class);
        }

        @Override
        protected boolean checkIntentCompleteness() {
            return intent.hasExtra(INTENT_URL) && intent.hasExtra(INTENT_TITLE);
        }

        public Builder setTitle(String title) {
            intent.putExtra(INTENT_TITLE, title);
            return this;
        }

        public Builder setUrl(String url) {
            intent.putExtra(INTENT_URL, url);
            return this;
        }
    }
}
