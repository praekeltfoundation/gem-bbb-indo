package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
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
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class LoginActivity extends DooitActivity {

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

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_login_login_button)
    public void login() {

        if (!detailsValid())
            return;
        hideKeyboard();
        authenticationManager.login(name.getText().toString(), password.getText().toString(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorResponse().getErrors())
                    Snackbar.make(buttonLogin, msg, Snackbar.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<AuthenticationResponse>() {
            @Override
            public void call(AuthenticationResponse authenticationResponse) {
                persisted.setCurrentUser(authenticationResponse.getUser());
                persisted.saveToken(authenticationResponse.getToken());
                MainActivity.Builder.create(LoginActivity.this).startActivityClearTop();
            }
        });
    }

    private boolean detailsValid() {
        return isNameValid() & isPasswordValid();
    }

    public boolean isNameValid() {
        boolean valid;
        valid = !TextUtils.isEmpty(name.getText());
        if (!valid) {
            nameHint.setText(R.string.login_example_name_error_1);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(R.string.login_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
    }


    public boolean isPasswordValid() {
        boolean valid;
        valid = !TextUtils.isEmpty(password.getText());
        if (!valid) {
            passwordHint.setText(R.string.login_example_password_error_1);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            passwordHint.setText(R.string.login_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
    }


    public static class Builder extends DooitActivityBuilder<LoginActivity.Builder> {
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
