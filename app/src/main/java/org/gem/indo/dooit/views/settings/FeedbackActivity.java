package org.gem.indo.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FeedbackManager;
import org.gem.indo.dooit.helpers.Connectivity.NetworkChangeReceiver;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.UserFeedback;
import org.gem.indo.dooit.models.enums.FeedbackType;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.settings.adapters.FeedbackTypeAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Rudolph Jacobs on 2016-12-05.
 */

public class FeedbackActivity extends DooitActivity {
    /*************
     * Variables *
     *************/

    public static final String TAG = "FeedbackActivity";
    public static final String ARG_TYPE = "feedback_type";

    Subscription feedbackSubscription = null;

    Unbinder unbinder = null;


    /************
     * Bindings *
     ************/

    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Inject
    FeedbackManager feedbackManager;

    @BindView(R.id.activity_feedback)
    View background;

    @BindView(R.id.activity_feedback_subject)
    Spinner subject;

    @BindView(R.id.activity_feedback_message)
    EditText message;

    @BindView(R.id.activity_feedback_submit)
    Button submission;


    /****************
     * Constructors *
     ****************/

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
            this.intent = new Intent(context, FeedbackActivity.class);
            return intent;
        }
    }


    /***********************
     * Lifecycle overrides *
     ***********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ((DooitApplication) getApplication()).component.inject(this);
        unbinder = ButterKnife.bind(this);

        // Populate feedback types
        FeedbackTypeAdapter feedbackTypeArrayAdapter = new FeedbackTypeAdapter(
                this,
                R.layout.item_view_spinner,
                R.layout.spinner_dropdown_item,
                FeedbackType.values()
        );
        subject.setAdapter(feedbackTypeArrayAdapter);
        Intent intent = getIntent();
        if (intent != null) {
            FeedbackType type = FeedbackType.getValueOf(intent.getIntExtra(ARG_TYPE, -1));
            if (type != null) {
                int i = feedbackTypeArrayAdapter.getPosition(type);
                subject.setSelection(i >= 0 && i < feedbackTypeArrayAdapter.getCount() ? i : 0);
                if (type.getValue() == FeedbackType.REPORT.getValue()) {
                    TextView mTextView = (TextView) findViewById(R.id.activity_feedback_heading);
                    mTextView.setText(getString(R.string.profile_help_report_problem));
                }
            }
        }

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!NetworkChangeReceiver.isOnline(getBaseContext())){
            disableUI();
        }
    }

    @Override
    protected void onStop() {
        if (feedbackSubscription != null) {
            feedbackSubscription.unsubscribe();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    /***************
     * Interaction *
     ***************/

    @OnClick(R.id.activity_feedback_submit)
    public void submit() {
        String msg = message == null ? null : message.getText().toString();
        FeedbackType type = (FeedbackType) subject.getSelectedItem();

        if (TextUtils.isEmpty(msg) || type == null) {
            Toast.makeText(
                    this,
                    "You need to pick a type and enter a message.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        UserFeedback feedback = new UserFeedback(msg, type);
        feedbackSubscription = feedbackManager.sendFeedback(feedback, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Log.d(TAG, "Could not submit feedback.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                FeedbackActivity.this,
                                "Sorry, but we couldn't send your feedback.",
                                Toast.LENGTH_SHORT
                        ).show();
                        FeedbackActivity.this.finish();
                    }
                });
            }
        }).subscribe(new Action1<UserFeedback>() {
            @Override
            public void call(UserFeedback userFeedback) {
                Log.d(TAG, "Feedback successfully submitted.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                FeedbackActivity.this,
                                "Thank you for your feedback!",
                                Toast.LENGTH_SHORT
                        ).show();
                        FeedbackActivity.this.finish();
                    }
                });
            }
        });
    }

    @Override
    public void onConnectionLost() {
        super.onConnectionLost();
        disableUI();
    }

    @Override
    public void onConnectionReestablished() {
        super.onConnectionReestablished();
        enableUI();
    }

    private void enableUI(){
        subject.setEnabled(true);
        message.setEnabled(true);
        submission.setEnabled(true);
    }

    private void disableUI(){
        subject.setEnabled(false);
        message.setEnabled(false);
        submission.setEnabled(false);
    }
}
