package org.gem.indo.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.gem.indo.dooit.BuildConfig;
import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.LanguageCodeHelper;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuildInfoActivity extends DooitActivity {

    @BindView(R.id.activity_build_info)
    View background;

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
