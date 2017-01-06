package org.gem.indo.dooit.views.web;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.AuthenticationManager;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.helpers.LanguageCodeHelper;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.onboarding.LoginActivity;
import org.gem.indo.dooit.views.settings.SettingsActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class MinimalWebViewActivity extends DooitActivity {

    private static final String INTENT_URL = "intent_webView_url";
    private static final String INTENT_TITLE = "intent_webView_title";
    private static final String INTENT_NO_CARET = "intent_noCaret_title";
    private static final String INTENT_WEBTIPS_SHARE = "intent_webtips_share";
    private boolean share = false;
    private boolean auth = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_settings_web_view)
    WebView webView;

    @BindView(R.id.activity_settings_web_progress)
    ProgressBar progressBar;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_web_view);
        ((DooitApplication) getApplication()).component.inject(this);
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

            if (getIntent().hasExtra(INTENT_WEBTIPS_SHARE))
                share = true;
            else
                share = false;

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

        Map<String, String> headers = new HashMap<String,String>();
        headers.put("Accept-Language", LanguageCodeHelper.getLanguage());

        if(persisted.hasToken()) {
//            headers.put("Authorization", persisted.getToken());
            auth = true;
        }

        webView.loadUrl(getIntent().getStringExtra(INTENT_URL),headers);
        Log.d("Web-Headers",headers.toString());
    }

    protected boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //check boolean to see if intent was set, if no intent for share then load correct menu
        if(share && auth){
            getMenuInflater().inflate(R.menu.menu_webtips_share_and_favourite, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else if(share) {
            getMenuInflater().inflate(R.menu.menu_webtips_share, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else{
            getMenuInflater().inflate(R.menu.menu_main,menu);
            return  super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_webtips_share:
                new SocialSharer(this).share(
                        this.getText(R.string.share_chooser_tip_title),
                        Uri.parse(getIntent().getStringExtra(INTENT_URL))
                );
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
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

        public Builder setWebTipShare() {
            intent.putExtra(INTENT_WEBTIPS_SHARE, "web_tip_share");
            return this;
        }
    }
}
