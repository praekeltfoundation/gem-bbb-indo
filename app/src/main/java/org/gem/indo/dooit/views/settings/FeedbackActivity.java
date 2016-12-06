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

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FeedbackManager;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.UserFeedback;
import org.gem.indo.dooit.models.enums.FeedbackType;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.settings.adapters.FeedbackTypeAdapter;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rudolph Jacobs on 2016-12-05.
 */

public class FeedbackActivity extends DooitActivity {
    /*************
     * Variables *
     *************/

    public static final String TAG = "FeedbackActivity";

    FeedbackType feedbackType = null;


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
            return new Intent(context, FeedbackActivity.class);
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
        ButterKnife.bind(this);

        // Populate feedback types
        FeedbackTypeAdapter feedbackTypeArrayAdapter = new FeedbackTypeAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(FeedbackType.values())
        );
        subject.setAdapter(feedbackTypeArrayAdapter);

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }


    /***************
     * Interaction *
     ***************/

    @OnClick(R.id.activity_feedback_submit)
    public void submit() {
        String msg = message == null ? null : message.toString();
        FeedbackType type = (FeedbackType) subject.getSelectedItem();

        if (TextUtils.isEmpty(msg)) return;
        if (type == null) return;

        UserFeedback feedback = new UserFeedback(message.toString(), (FeedbackType) subject.getSelectedItem());
        feedbackManager.sendFeedback(feedback, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Log.d(TAG, "Could not submit feedback.");
            }
        });
    }
}
