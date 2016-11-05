package com.nike.dooit.views.onboarding;

import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.widget.Button;

import com.nike.dooit.R;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.views.DooitActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends DooitActivity {

    @BindView(R.id.activity_login_login_button)
    Button buttonLogin;

    @Inject
    AuthenticationManager authenticationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_login_login_button)
    public void login() {

    }

}
