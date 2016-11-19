package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingWebActivity extends DooitActivity {

    private static final String INTENT_URL = "intent_webView_url";
    private static final String INTENT_TITLE = "intent_webView_title";

    @BindView(R.id.activity_onboarding_web_view)
    WebView webView;

    @BindView(R.id.activity_onboarding_web_progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_web_view);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

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

    public static class Builder extends DooitActivityBuilder<OnboardingWebActivity.Builder> {

        public Builder(Context context) {
            super(context);
        }

        public static OnboardingWebActivity.Builder create(Context context) {
            return new OnboardingWebActivity.Builder(context);
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, OnboardingWebActivity.class);
        }

        @Override
        protected boolean checkIntentCompleteness() {
            return intent.hasExtra(INTENT_URL) && intent.hasExtra(INTENT_TITLE);
        }

        public OnboardingWebActivity.Builder setTitle(String title) {
            intent.putExtra(INTENT_TITLE, title);
            return this;
        }

        public OnboardingWebActivity.Builder setUrl(String url) {
            intent.putExtra(INTENT_URL, url);
            return this;
        }
    }
}
