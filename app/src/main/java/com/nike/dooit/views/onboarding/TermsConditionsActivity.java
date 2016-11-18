package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.nike.dooit.Constants;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsConditionsActivity extends DooitActivity {

    @BindView(R.id.activity_terms_conditions_web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        webView.clearHistory();
        webView.loadUrl(Constants.TERMS_URL);
    }

    public static class Builder extends DooitActivityBuilder<TermsConditionsActivity.Builder> {

        public Builder(Context context) {
            super(context);
        }

        public static TermsConditionsActivity.Builder create(Context context) {
            return new TermsConditionsActivity.Builder(context);
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, TermsConditionsActivity.class);
        }
    }
}
