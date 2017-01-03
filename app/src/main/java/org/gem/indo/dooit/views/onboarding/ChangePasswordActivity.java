package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AuthenticationManager;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.validatior.UserValidator;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by chris on 2016-11-21.
 */

public class ChangePasswordActivity extends DooitActivity {


    @BindView(R.id.activity_change_password_edit_text)
    EditText password;

    @BindView(R.id.activity_change_password_example_text_edit)
    TextView passwordHint;

    @BindView(R.id.activity_change_password_old_edit_text)
    EditText passwordOld;

    @BindView(R.id.activity_change_password_button)
    Button changePasswordButton;

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    UserManager userManager;

    @Inject
    Persisted persisted;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_change_password);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @OnClick(R.id.activity_change_password_button)
    public void changePassword() {
        hideKeyboard();
        UserValidator uValidator = new UserValidator();
        if (!uValidator.isPasswordValid(this.password.getText().toString())){
            passwordHint.setText(uValidator.getResponseText());
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
            return;
        }
        else{
            passwordHint.setText(R.string.reg_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()));
        }


        final User user = persisted.getCurrentUser();
        final String newPassword = this.password.getText().toString();
        final String oldPassword = this.passwordOld.getText().toString();
        userManager.changePassword(user.getId(), oldPassword, newPassword, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(changePasswordButton, msg, Snackbar.LENGTH_LONG).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                Snackbar.make(changePasswordButton, R.string.profile_change_password_success, Snackbar.LENGTH_SHORT).show();
                user.setPassword(newPassword);
                ChangePasswordActivity.this.persisted.setCurrentUser(user);
                login(user, newPassword);
            }
        });
    }

    void login(User user, String newPassword) {
        authenticationManager.login(user.getUsername(), newPassword, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(changePasswordButton, msg, Snackbar.LENGTH_LONG).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoginActivity.Builder.create(ChangePasswordActivity.this)
                                .startActivityClearTop();
                    }
                }, 2000);
            }
        }).subscribe(new Action1<AuthenticationResponse>() {
            @Override
            public void call(AuthenticationResponse response) {
                persisted.setCurrentUser(response.getUser());
                persisted.saveToken(response.getToken());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChangePasswordActivity.this.finish();
                    }
                }, 2000);
            }
        });
    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangePasswordActivity.Builder create(Context context) {
            ChangePasswordActivity.Builder builder = new ChangePasswordActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangePasswordActivity.class);
        }
    }
}