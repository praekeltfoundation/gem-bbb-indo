package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.custom.WigglyEditText;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

public class ChangeSecurityQuestionActivity extends DooitActivity {
    /*************
     * Constants *
     *************/

    public static final int minFieldLength = 6;


    /*************
     * Variables *
     *************/

    Unbinder unbinder = null;
    Subscription securityQuestionSubscription = null;


    /************
     * Bindings *
     ************/

    @Inject
    UserManager userManager;

    @BindView(R.id.activity_onboarding_change_secq_background)
    View background;

    @BindView(R.id.activity_onboarding_change_secq_question_textbox)
    WigglyEditText questionBox;

    @BindView(R.id.activity_onboarding_change_secq_answer_textbox)
    WigglyEditText answerBox;

    @BindView(R.id.activity_onboarding_change_secq_button)
    Button submitButton;


    /****************
     * Constructors *
     ****************/

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangeSecurityQuestionActivity.Builder create(Context context) {
            ChangeSecurityQuestionActivity.Builder builder = new ChangeSecurityQuestionActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangeSecurityQuestionActivity.class);
        }
    }


    /***********************
     * Lifecycle overrides *
     ***********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_change_security_question);
        ((DooitApplication) getApplication()).component.inject(this);
        unbinder = ButterKnife.bind(this);
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }

    @Override
    protected void onStop() {
        if (securityQuestionSubscription != null) {
            securityQuestionSubscription.unsubscribe();
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


    @OnClick(R.id.activity_onboarding_change_secq_button)
    protected void submit() {
        boolean valid = true;
        String question = questionBox.getEditText();
        String answer = answerBox.getEditText();

        if (TextUtils.isEmpty(question)) {
            valid = false;
            questionBox.setMessageText(getString(R.string.label_required));
        } else if (question.length() < minFieldLength) {
            valid = false;
            questionBox.setMessageText(String.format(getString(org.gem.indo.dooit.R.string.msg_security_question_too_short), minFieldLength));
        }

        if (TextUtils.isEmpty(answer)) {
            valid = false;
            answerBox.setMessageText(getString(R.string.label_required));
        }

        if (!valid) {
            Snackbar.make(submitButton, R.string.msg_fix_indicated_fields, Snackbar.LENGTH_SHORT).show();
            return;
        }

        securityQuestionSubscription = userManager.alterSecurityQuestion(question, answer, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(submitButton, R.string.security_question_could_not_send, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(submitButton, R.string.security_question_change_successful, Snackbar.LENGTH_SHORT).show();
                        ChangeSecurityQuestionActivity.this.finish();
                    }
                });
            }
        });
    }
}
