package org.gem.indo.dooit.views.web;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.LanguageCodeHelper;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class MinimalWebViewActivity extends DooitActivity {

    private static final String INTENT_URL = "intent_webView_url";
    private static final String INTENT_TITLE = "intent_webView_title";
    private static final String INTENT_NO_CARET = "intent_noCaret_title";
    private static final String INTENT_WEBTIPS_SHARE = "intent_webtips_share";
    private static final String INTENT_WEBTIPS_ID = "intent_webtips_id";
    private static final String INTENT_SCREEN_NAME = "screen_name";
    private boolean share = false;
    private boolean auth = false;
    private int tipID;
    private boolean isFavourite = false;
    private MenuItem favView;
    private String title;

    /**
     * Used to distinguish the web view usage in analytics.
     */
    private String screenName;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.tips_article_add_fav)
    String addFavArticleText;

    @BindString(R.string.tips_article_remove_fav)
    String removeFavArticleText;

    @BindView(R.id.activity_settings_web_view)
    WebView webView;

    @BindView(R.id.activity_settings_web_progress)
    ProgressBar progressBar;

    @Inject
    Persisted persisted;

    @Inject
    TipManager tipManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (getIntent().hasExtra(INTENT_NO_CARET)) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_d_back_caret_pink);
            }

            if (getIntent().hasExtra(INTENT_TITLE))
                setTitle(getIntent().getStringExtra(INTENT_TITLE));
            else
                setTitle("");

            // Title is in HTML
            actionBar.setTitle("");

            if (getIntent().hasExtra(INTENT_WEBTIPS_ID))
                setTipID(getIntent().getIntExtra(INTENT_WEBTIPS_ID, 0));

            share = getIntent().getBooleanExtra(INTENT_WEBTIPS_SHARE, false);

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

        if (!hasInternetConnection()) // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

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

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Language", LanguageCodeHelper.getLanguage());

        auth = persisted.hasToken();

        webView.loadUrl(getIntent().getStringExtra(INTENT_URL), headers);
        Log.d("Web-Headers", headers.toString());

        // Usage Analytics
        if (getIntent().hasExtra(INTENT_SCREEN_NAME))
            screenName = getIntent().getStringExtra(INTENT_SCREEN_NAME);
    }


    protected boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private boolean isFavourite() {
        return isFavourite;
    }

    public void setTipID(int id) {
        tipID = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTipID() {
        return this.tipID;
    }

    public String getWebViewTitle() {
        return this.title;
    }

    public void favouriteWebTip(final View view) {
        if (isFavourite()) {
            tipManager.unfavourite(getTipID(), new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {
                    Log.d("Favourite in WebView", "Tip favourite status could not be set " + error.getMessage());
                }
            }).subscribe(new Action1<EmptyResponse>() {
                @Override
                public void call(EmptyResponse emptyResponse) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            setFavourite(false);
                            persisted.clearFavourites();
                            Toast.makeText(MinimalWebViewActivity.this, String.format(removeFavArticleText,
                                    getWebViewTitle()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            tipManager.favourite(getTipID(), new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {
                    Log.d("Favourite in WebView", "Tip favourite status could not be set " + error.getMessage());
                }
            }).subscribe(new Action1<EmptyResponse>() {
                @Override
                public void call(EmptyResponse emptyResponse) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            setFavourite(true);
                            persisted.clearFavourites();
                            Toast.makeText(MinimalWebViewActivity.this, String.format(addFavArticleText,
                                    getWebViewTitle()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public String getScreenName() {
        if (!TextUtils.isEmpty(screenName))
            return screenName;
        return super.getScreenName();
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        if (isFavourite)
            favView.setIcon(ContextCompat.getDrawable(MinimalWebViewActivity.this, R.drawable.ic_d_heart_pink));
        else
            favView.setIcon(ContextCompat.getDrawable(MinimalWebViewActivity.this, R.drawable.ic_d_heart_pink_inverted));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //check boolean to see if intent was set, if no intent for share then load correct menu
        if (share) {
            getMenuInflater().inflate(R.menu.menu_webtips_share, menu);
            favView = menu.findItem(R.id.menu_webtips_favourite_icon);
            if (!auth) favView.setVisible(false);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_webtips_share_icon:
                new SocialSharer(this).share(
                        this.getText(R.string.share_chooser_tip_title),
                        Uri.parse(getIntent().getStringExtra(INTENT_URL))
                );
                break;
            case R.id.menu_webtips_favourite_icon:
                favouriteWebTip(this.webView);
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

        public Builder setScreenName(String screenName) {
            intent.putExtra(INTENT_SCREEN_NAME, screenName);
            return this;
        }

        public Builder setWebTipShare() {
            intent.putExtra(INTENT_WEBTIPS_SHARE, true);
            return this;
        }

        public Builder setWebTipId(int id) {
            intent.putExtra(INTENT_WEBTIPS_ID, id);
            return this;
        }
    }
}
