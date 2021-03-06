package org.gem.indo.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import org.gem.indo.dooit.helpers.social.SocialApps;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
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

    @BindView(R.id.activity_build_info_date)
    TextView dateTextView;

    @BindView(R.id.activity_build_info_variant)
    TextView variantTextView;

    @BindView(R.id.activity_build_info_server)
    TextView serverTextView;

    @BindView(R.id.activity_build_info_language)
    TextView languageTextView;

    @BindView(R.id.activity_build_info_social_facebook_flag)
    TextView facebookFlag;

    @BindView(R.id.activity_build_info_social_twitter_flag)
    TextView twitterFlag;

    @BindView(R.id.activity_build_info_social_line_flag)
    TextView lineFlag;

    @BindView(R.id.activity_build_info_social_whatsapp_flag)
    TextView whatsappFlag;

    @BindView(R.id.activity_build_info_social_instagram_flag)
    TextView instagramFlag;

    @BindString(R.string.profile_build_info_social_apps_installed)
    String installedText;

    @BindString(R.string.profile_build_info_social_apps_not_found)
    String notFoundText;

    @BindString(R.string.profile_build_info_social_apps_share_text)
    String shareTypeText;

    @BindString(R.string.profile_build_info_social_apps_share_image)
    String shareTypeImage;

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

        // Build Datetime
        dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date(BuildConfig.TIMESTAMP)));

        // Variant
        variantTextView.setText(BuildConfig.FLAVOR);

        // Server
        serverTextView.setText(Constants.BASE_URL
                .replace("http://", "")
                .replace("https://", ""));

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.grey_back, R.color.grey_fore, background);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // These fields can change while the app is minimised

        // Language
        languageTextView.setText(LanguageCodeHelper.getLanguage());

        // Social
        SocialSharer sharer = new SocialSharer(this);

        facebookFlag.setText(sharer.isInstalled(SocialApps.FACEBOOK) ? installedText : notFoundText);
        twitterFlag.setText(sharer.isInstalled(SocialApps.TWITTER) ? installedText : notFoundText);
        lineFlag.setText(sharer.isInstalled(SocialApps.LINE) ? installedText : notFoundText);
        whatsappFlag.setText(sharer.isInstalled(SocialApps.WHATSAPP) ? installedText : notFoundText);
        instagramFlag.setText(sharer.isInstalled(SocialApps.INSTAGRAM) ? installedText : notFoundText);

        // Using sets to get rid of duplicates
        Set<String> textApps = new HashSet<>(sharer.query(SocialSharer.TYPE_TEXT));
        Set<String> imageApps = new HashSet<>(sharer.query(SocialSharer.TYPE_IMAGE_ANY));

        for (String packageName : textApps)
            switch (packageName) {
                case SocialApps.FACEBOOK:
                    facebookFlag.setText(facebookFlag.getText() + ", " + shareTypeText);
                    break;
                case SocialApps.TWITTER:
                    twitterFlag.setText(twitterFlag.getText() + ", " + shareTypeText);
                    break;
                case SocialApps.LINE:
                    lineFlag.setText(lineFlag.getText() + ", " + shareTypeText);
                    break;
                case SocialApps.WHATSAPP:
                    whatsappFlag.setText(whatsappFlag.getText() + ", " + shareTypeText);
                    break;
            }

        for (String packageName : imageApps)
            if (packageName.equals(SocialApps.INSTAGRAM))
                instagramFlag.setText(instagramFlag.getText() + ", " + shareTypeImage);
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
