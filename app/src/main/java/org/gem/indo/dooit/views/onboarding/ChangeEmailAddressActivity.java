package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.validatior.UserValidator;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

import static java.util.prefs.Preferences.MAX_NAME_LENGTH;

/**
 * Created by frede on 2016/12/22.
 */

public class ChangeEmailAddressActivity extends DooitActivity {
    @BindString(R.string.profile_change_email_success)
    String successText;

    @BindView(R.id.activity_change_email_text_edit)
    EditText email;

    @BindView(R.id.activity_change_email_example_text_edit)
    TextView emailHint;

    @BindView(R.id.activity_change_email_button)
    Button changeEmailButton;

    @Inject
    UserManager userManager;

    @Inject
    Persisted persisted;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_onboarding_change_email);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @OnClick(R.id.activity_change_email_button)
    public void changeEmail() {
        hideKeyboard();
        UserValidator uValidator = new UserValidator();
        if(!uValidator.isEmailValid(this.email.getText().toString())) {
            emailHint.setText(uValidator.getResponseText());
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
            return;
        }
        else{
            emailHint.setText(R.string.reg_example_email);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()));
        }
        final User user = persisted.getCurrentUser();
        final String email = this.email.getText().toString();
        userManager.updateUserEmail(user.getId(),email,new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(changeEmailButton, msg, Snackbar.LENGTH_LONG).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                changeEmailButton.post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(changeEmailButton, String.format(successText, email), Snackbar.LENGTH_LONG).show();
                    }
                });
                user.setEmail(email);
                ChangeEmailAddressActivity.this.persisted.setCurrentUser(user);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChangeEmailAddressActivity.this.finish();
                    }
                }, 2000);
            }
        });

    }
    public boolean isEmailValid() {
        boolean valid = true;
        String emailText = email.getText().toString();
        if (TextUtils.isEmpty(emailText)) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_1);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (emailText.contains(" ")) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_2);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_3);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (emailText.length() > MAX_NAME_LENGTH) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_4);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public static class Builder extends DooitActivityBuilder<ChangeEmailAddressActivity.Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangeEmailAddressActivity.Builder create(Context context) {
            ChangeEmailAddressActivity.Builder builder = new ChangeEmailAddressActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangeEmailAddressActivity.class);
        }
    }
}
