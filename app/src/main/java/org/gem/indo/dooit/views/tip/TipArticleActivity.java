package org.gem.indo.dooit.views.tip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TipArticleActivity extends DooitActivity {

    public static final String ARTICLE_URL = "articleUrl";

    @BindView(org.gem.indo.dooit.R.id.activity_tip_article_web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_tip_article);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        Bundle args = getIntent().getExtras();

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(args.getString(ARTICLE_URL));
    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        public Builder(Context context) {
            super(context);
        }

        public static TipArticleActivity.Builder create(Context context) {
            return new TipArticleActivity.Builder(context);
        }

        public TipArticleActivity.Builder putArticleUrl(String url) {
            intent.putExtra(TipArticleActivity.ARTICLE_URL, url);
            return this;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, TipArticleActivity.class);
        }
    }
}
