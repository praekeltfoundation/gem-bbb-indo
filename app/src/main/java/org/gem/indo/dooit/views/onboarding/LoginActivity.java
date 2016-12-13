package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AuthenticationManager;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.MainActivity;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.functions.Action0;
import rx.functions.Action1;

public class LoginActivity extends DooitActivity {

    @BindView(R.id.activity_login)
    View background;

    @BindView(R.id.activity_login_login_button)
    Button buttonLogin;

    @BindView(R.id.activity_login_name_edit_text)
    EditText name;

    @BindView(R.id.activity_login_password_edit_text)
    EditText password;

    @BindView(R.id.activity_login_name_example_text_view)
    TextView nameHint;

    @BindView(R.id.activity_login_password_example_text_view)
    TextView passwordHint;

    @BindView(R.id.activity_login_forgot_text_view)
    TextView forgotLink;

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);
        setContentView(org.gem.indo.dooit.R.layout.activity_login);
        ButterKnife.bind(this);
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }

    @OnClick(R.id.activity_login_login_button)
    public void login() {
        if (!detailsValid())
            return;

        hideKeyboard();
        showProgressDialog(R.string.login_progress_dialog_message);
        authenticationManager.login(name.getText().toString(), password.getText().toString(), new DooitErrorHandler() {
            @Override
            public void onError(final DooitAPIError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error != null && error.getErrorResponse() != null) {
                            for (String msg : error.getErrorResponse().getErrors()) {
                                Snackbar.make(buttonLogin, msg, Snackbar.LENGTH_LONG).show();
                            }
                        }else if(error.getCause() instanceof SocketTimeoutException){
                            Snackbar.make(buttonLogin, R.string.connection_timed_out, Snackbar.LENGTH_LONG).show();
                        }
                        dismissDialog();
                    }
                });
            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                dismissDialog();
            }
        }).subscribe(new Action1<AuthenticationResponse>() {
            @Override
            public void call(AuthenticationResponse authenticationResponse) {
                persisted.setCurrentUser(authenticationResponse.getUser());
                persisted.saveToken(authenticationResponse.getToken());
                persisted.setNewBotUser(false);
                NotificationAlarm.setAlarm(LoginActivity.this);
                MainActivity.Builder.create(LoginActivity.this).startActivityClearTop();
            }
        });
    }

    @OnEditorAction(R.id.activity_login_password_edit_text)
    boolean imeLogin(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            login();
        }
        return true;
    }

    @OnClick(R.id.activity_login_forgot_text_view)
    protected void forgot() {
        PasswordResetActivity.Builder.create(this).startActivity();
    }

    private boolean detailsValid() {
        return isNameValid() & isPasswordValid();
    }

    public boolean isNameValid() {
        boolean valid;
        valid = !TextUtils.isEmpty(name.getText());
        if (!valid) {
            nameHint.setText(org.gem.indo.dooit.R.string.login_example_name_error_1);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(org.gem.indo.dooit.R.string.login_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isPasswordValid() {
        boolean valid;
        valid = !TextUtils.isEmpty(password.getText());
        if (!valid) {
            passwordHint.setText(org.gem.indo.dooit.R.string.login_example_password_error_1);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            passwordHint.setText(org.gem.indo.dooit.R.string.login_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static LoginActivity.Builder create(Context context) {
            Builder builder = new Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, LoginActivity.class);
        }

    }

}
