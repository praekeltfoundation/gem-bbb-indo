package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
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
import org.gem.indo.dooit.helpers.auth.InvalidTokenRedirectHelper;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.TextSpannableHelper;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.validation.UserValidator;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.MainActivity;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.functions.Action0;
import rx.functions.Action1;

public class LoginActivity extends DooitActivity {

    public static final String INTENT_WAS_REDIRECT = "was_redirect";

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

    @BindView(R.id.activity_login_not_registered)
    TextView notRegister;

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    Persisted persisted;

    @Inject
    InvalidTokenRedirectHelper invalidTokenRedirectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);
        setContentView(org.gem.indo.dooit.R.layout.activity_login);
        ButterKnife.bind(this);

        String stringRegister = getResources().getString(R.string.login_not_registered);
        TextSpannableHelper spanRegistrationHelper = new TextSpannableHelper();

        notRegister.setText(spanRegistrationHelper.styleText(this, R.style.AppTheme_TextView_Bold_Small_Accented, stringRegister));

        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean wasRedirected = getIntent().getBooleanExtra(INTENT_WAS_REDIRECT,false);
        if(wasRedirected){
            Snackbar.make(buttonLogin, R.string.login_redirect_message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        invalidTokenRedirectHelper.setLoginStarted(false);
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
                        } else if (error.getCause() instanceof SocketTimeoutException) {
                            Snackbar.make(buttonLogin, R.string.connection_timed_out, Snackbar.LENGTH_LONG).show();
                        } else if (error.getCause() instanceof UnknownHostException) {
                            Snackbar.make(buttonLogin, R.string.connection_error, Snackbar.LENGTH_LONG).show();
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
                persisted.saveUserUUID(authenticationResponse.getUserUUID());
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

    @OnClick(R.id.activity_login_not_registered)
    protected void register() {
        RegistrationActivity.Builder.create(this).startActivityClearTop();
    }

    private boolean detailsValid() {
        boolean valid = true;
        UserValidator uValidator = new UserValidator();

        if (!uValidator.isNameValid(name.getText().toString())) {
            valid = false;
            nameHint.setText(uValidator.getResponseText());
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(R.string.reg_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()));
        }

        if (!uValidator.isPasswordValid(password.getText().toString())) {
            valid = false;
            passwordHint.setText(uValidator.getResponseText());
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            passwordHint.setText(R.string.reg_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()));
        }

        return valid;
    }

    public static class Builder extends DooitActivityBuilder<Builder> {

        protected Builder(Context context) {
            super(context);
        }

        protected Builder(Context context, boolean wasRedirected ) {
            super(context);
            intent.putExtra(INTENT_WAS_REDIRECT, wasRedirected);
        }

        public static LoginActivity.Builder create(Context context) {
            Builder builder = new Builder(context);
            return builder;
        }

        public static LoginActivity.Builder create(Context context, boolean wasRedirected) {
            Builder builder = new Builder(context, wasRedirected);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, LoginActivity.class);
        }

    }

}
