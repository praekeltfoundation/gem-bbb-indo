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
import org.gem.indo.dooit.helpers.Connectivity.NetworkChangeReceiver;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.models.enums.FeedbackType;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.RootActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.onboarding.ChangeEmailAddressActivity;
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

    private static final String SCREEN_NAME_TERMS = "Terms & Conditions";
    private static final String SCREEN_NAME_PRIVACY = "Privacy Policy";

    @BindView(R.id.activity_settings)
    View background;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView title;

    @BindView(R.id.settings_notifications_challenge_available)
    CompoundButton challengeAvailableSwitch;

    @BindView(R.id.settings_notifications_challenge_available_reminder)
    CompoundButton challengeReminderSwitch;

    @BindView(R.id.settings_notifications_challenge_completion_reminder)
    CompoundButton challengeCompletionSwitch;

    @BindView(R.id.settings_notifications_saving_reminder)
    CompoundButton savingReminderSwitch;

    @BindView(R.id.settings_notifications_goal_deadline_missed)
    CompoundButton deadlineMissedSwitch;

    @BindView(R.id.settings_notifications_survey_available)
    CompoundButton surveyAvailableSwitch;

    @BindView(R.id.settings_notifications_challenge_winner)
    CompoundButton checkChallengeWinnerSwitch;

    @BindView(R.id.settings_notifications_custom_notification)
    CompoundButton customNotificationSwitch;

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
        challengeReminderSwitch.setChecked(persisted.shouldNotify(NotificationType.CHALLENGE_REMINDER));
        challengeCompletionSwitch.setChecked(persisted.shouldNotify(NotificationType.CHALLENGE_COMPLETION_REMINDER));
        savingReminderSwitch.setChecked(persisted.shouldNotify(NotificationType.SAVING_REMINDER));
        deadlineMissedSwitch.setChecked(persisted.shouldNotify(NotificationType.GOAL_DEADLINE_MISSED));
        surveyAvailableSwitch.setChecked(persisted.shouldNotify(NotificationType.SURVEY_AVAILABLE));
        checkChallengeWinnerSwitch.setChecked(persisted.shouldNotify(NotificationType.CHALLENGE_WINNER));
        customNotificationSwitch.setChecked(persisted.shouldNotify(NotificationType.AD_HOC));
        /*
        if(!NetworkChangeReceiver.isOnline(getBaseContext())){
            disableUI();
        }
        */
    }

    @OnClick(R.id.settings_account_change_name)
    public void changeName(View view) {
        ChangeNameActivity.Builder.create(this).startActivity();
    }

    @OnClick(R.id.settings_account_change_password)
    public void changePassword(View view) {
        ChangePasswordActivity.Builder.create(this).startActivity();
    }

    @OnClick(R.id.settings_account_change_email)
    public void changeEmail(View view) {
        ChangeEmailAddressActivity.Builder.create(this).startActivity();
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

    @OnCheckedChanged({R.id.settings_notifications_challenge_available_reminder})
    public void checkChallengeReminder(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.CHALLENGE_REMINDER, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_challenge_completion_reminder})
    public void checkCHallengeCompletion(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.CHALLENGE_COMPLETION_REMINDER, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_saving_reminder})
    public void checkSavingReminder(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.SAVING_REMINDER, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_goal_deadline_missed})
    public void checkDeadlineMissedReminder(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.GOAL_DEADLINE_MISSED, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_survey_available})
    public void checkSurveyReminder(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.SURVEY_AVAILABLE, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_challenge_winner})
    public void checkChallengeWinner(CompoundButton button, boolean checked) {
        persisted.setNotify(NotificationType.CHALLENGE_WINNER, checked);
    }

    @OnCheckedChanged({R.id.settings_notifications_custom_notification})
    public void checkCustomNotification(CompoundButton buttons, boolean checked) {
        persisted.setNotify(NotificationType.AD_HOC, checked);
    }

    @OnClick({R.id.settings_about_terms})
    public void terms(View view) {
        MinimalWebViewActivity.Builder.create(this)
                //.setTitle(getString(org.gem.indo.dooit.R.string.title_activity_terms_and_conditions))
                .setUrl(Constants.TERMS_URL)
                .setScreenName(SCREEN_NAME_TERMS)
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
                .setScreenName(SCREEN_NAME_PRIVACY)
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

    /*
    private void enableUI(){
        challengeAvailableSwitch.setEnabled(true);
        savingReminderSwitch.setEnabled(true);
    }

    private void disableUI(){
        challengeAvailableSwitch.setEnabled(false);
        savingReminderSwitch.setEnabled(false);
    }
    */

    @Override
    public void onConnectionLost() {
        super.onConnectionLost();
        //disableUI();
    }

    @Override
    public void onConnectionReestablished() {
        super.onConnectionReestablished();
        //enableUI();
    }
}
