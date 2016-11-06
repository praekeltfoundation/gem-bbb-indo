package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.responses.AuthenticationResponse;
import com.nike.dooit.util.Persisted;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;
import com.nike.dooit.views.main.MainActivity;

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
        authenticationManager.login(name.getText().toString(), password.getText().toString(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<AuthenticationResponse>() {
            @Override
            public void call(AuthenticationResponse authenticationResponse) {
                persisted.setCurrentUser(authenticationResponse.getUser());
                persisted.saveToken(authenticationResponse.getToken());
                MainActivity.Builder.create(LoginActivity.this).startActivity();
            }
        });
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
