package org.gem.indo.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.gem.indo.dooit.BuildConfig;
import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.LanguageCodeHelper;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuildInfoActivity extends DooitActivity {

    @BindView(R.id.activity_build_info)
    View background;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_build_info_app_id)
    TextView appIdTextView;

    @BindView(R.id.activity_build_info_version)
    TextView versionTextView;

    @BindView(R.id.activity_build_info_variant)
    TextView variantTextView;

    @BindView(R.id.activity_build_info_server)
    TextView serverTextView;

    @BindView(R.id.activity_build_info_language)
    TextView languageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_info);
        ButterKnife.bind(this);

        // Animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // Toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_d_back_caret_pink);
            actionBar.setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                }
            });
        }

        // App Id
        appIdTextView.setText(BuildConfig.APPLICATION_ID);

        // Version
        versionTextView.setText(BuildConfig.VERSION_NAME);

        // Variant
        variantTextView.setText(BuildConfig.FLAVOR);

        // Server
        serverTextView.setText(Constants.BASE_URL);

        // Language
        languageTextView.setText(LanguageCodeHelper.getLanguage());

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.grey_back, R.color.grey_fore, background);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public static class Builder extends DooitActivityBuilder<Builder> {

        public Builder(Context context) {
            super(context);
        }

        public static BuildInfoActivity.Builder create(Context context) {
            return new BuildInfoActivity.Builder(context);
        }

        @Override
        protected Intent createIntent(Context context) {
            intent = new Intent(context, BuildInfoActivity.class);
            return intent;
        }
    }
}
