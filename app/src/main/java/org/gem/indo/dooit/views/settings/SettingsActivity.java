package org.gem.indo.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.models.enums.FeedbackType;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.RootActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.onboarding.ChangeNameActivity;
import org.gem.indo.dooit.views.onboarding.ChangePasswordActivity;
import org.gem.indo.dooit.views.onboarding.ChangeSecurityQuestionActivity;
import org.gem.indo.dooit.views.web.MinimalWebViewActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class SettingsActivity extends DooitActivity {

    @BindView(R.id.activity_settings)
    View background;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView title;

    @BindView(R.id.settings_notifications_challenge_available)
    CompoundButton challengeAvailableSwitch;

    @BindView(R.id.settings_notifications_saving_reminder)
    CompoundButton savingReminderSwitch;

    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_settings);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        // Animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.grey_back, R.color.grey_fore, background);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        challengeAvailableSwitch.setChecked(persisted.shouldNotify(NotificationType.CHALLENGE_AVAILABLE));
        savingReminderSwitch.setChecked(persisted.shouldNotify(NotificationType.SAVING_REMINDER));
    }

    @OnClick(R.id.settings_account_change_name)
    public void changeName(View view) {
        ChangeNameActivity.Builder.create(this).startActivity();
    }

    @OnClick(R.id.settings_account_change_password)
    public void changePassword(View view) {
        ChangePasswordActivity.Builder.create(this).startActivity();
    }

    @OnClick(R.id.settings_account_change_security_question)
    public void changeSecurityQuestion(View view) {
        ChangeSecurityQuestionActivity.Builder.create(this).startActivity();
    }

    @OnClick({R.id.settings_account_sign_out})
    public void signOut(View view) {
        NotificationAlarm.cancelAlarm(this);
        dooitSharedPreferences.clear();
        persisted.setNewBotUser(false); // Keep for next log in
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnCheckedChanged({R.id.settings_notifications_challenge_available})
    public void checkChallengeAvailable(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.CHALLENGE_AVAILABLE, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_saving_reminder})
    public void checkSavingReminder(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.SAVING_REMINDER, checked);
    }

    @OnClick({R.id.settings_about_terms})
    public void terms(View view) {
        MinimalWebViewActivity.Builder.create(this)
                //.setTitle(getString(org.gem.indo.dooit.R.string.title_activity_terms_and_conditions))
                .setUrl(Constants.TERMS_URL)
                .startActivity();
    }

    @OnClick(value = {R.id.settings_about_feedback, R.id.settings_about_report})
    public void feedback(View view) {
        FeedbackActivity.Builder nextActivity = FeedbackActivity.Builder.create(this);
        Intent intent = nextActivity.createIntent(this);
        switch (view.getId()) {
            case R.id.settings_about_feedback:
                intent.putExtra(FeedbackActivity.ARG_TYPE, FeedbackType.GENERAL.getValue());
                break;
            case R.id.settings_about_report:
                intent.putExtra(FeedbackActivity.ARG_TYPE, FeedbackType.REPORT.getValue());
                break;
        }
        nextActivity.startActivity();
    }

    @OnClick({R.id.settings_about_privacy})
    public void privacy(View view) {
        MinimalWebViewActivity.Builder.create(this)
                //.setTitle(getString(org.gem.indo.dooit.R.string.title_activity_privacy_policy))
                .setUrl(Constants.PRIVACY_URL)
                .startActivity();
    }

    @OnClick({R.id.settings_about_build_info})
    public void buildInfo(View view) {
        BuildInfoActivity.Builder.create(this)
                .startActivity();
    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static Builder create(Context context) {
            Builder builder = new Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            intent = new Intent(context, SettingsActivity.class);
            return intent;
        }

    }
}
