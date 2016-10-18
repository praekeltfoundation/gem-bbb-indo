package com.rr.rgem.gem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.service.AuthService;
import com.rr.rgem.gem.service.CMSService;
import com.rr.rgem.gem.service.ErrorUtil;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.service.model.AuthLogin;
import com.rr.rgem.gem.service.model.AuthToken;
import com.rr.rgem.gem.service.model.User;
import com.rr.rgem.gem.views.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ApplicationActivity {

    private final static String TAG = "LoginActivity";

    GEMNavigation navigation;
    CMSService service;
    AuthService authService;
    Persisted persist;
    View loginScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new GEMNavigation(this);
        loginScreen = navigation.addLayout(R.layout.user_login);

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        service = factory.createService(CMSService.class);
        authService = factory.createAuthService();
        persist = new Persisted(this);

        User user = persist.loadUser();
        if (user.getUsername() != null) {
            EditText editMobile = (EditText) findViewById(R.id.editTextMobile);
            editMobile.setText(user.getUsername());
        }

        final ErrorUtil errorUtil = new ErrorUtil(factory.createRetrofit());

        Button button = (Button) findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast(LoginActivity.this, "Login clicked");
                TextView editMobile = (TextView) LoginActivity.this.findViewById(R.id.editTextMobile);
                TextView editPassword = (TextView) LoginActivity.this.findViewById(R.id.editTextPassword);
                String mobile = editMobile.getText().toString();
                String password = editPassword.getText().toString();

                if (!mobileValid(mobile) || !passwordValid(password)) {
                    return;
                }

                AuthLogin login = new AuthLogin(mobile, password);

                authService.createToken(login).enqueue(new Callback<AuthToken>() {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        Utils.toast(LoginActivity.this, "Response");
                        Log.d(LoginActivity.TAG, String.format("Login Status %d", response.code()));
                        if (response.isSuccessful()) {
                            Log.d(LoginActivity.TAG, "Login Successful " + response.body().toString());
                            LoginActivity.this.persist.saveToken(response.body());
                        } else {
                            ErrorUtil.WebServiceError error = errorUtil.parseError(response);
                            Log.d(LoginActivity.TAG, "API Errors: " + error.getNonFieldErrorsJoined() + " " + error.getDetail());
                            Utils.toast(LoginActivity.this, error.getNonFieldErrorsJoined());
                        }


                    }

                    @Override
                    public void onFailure(Call<AuthToken> call, Throwable t) {
                        Log.e(LoginActivity.TAG, "Create Token HTTP Failure", t);
                    }
                });

                //LoginActivity.this.finish();
            }
        });
    }

    boolean mobileValid(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            Utils.toast(this, "Mobile number is empty");
            return false;
        }
        return true;
    }

    boolean passwordValid(String password) {
        if (password == null || password.isEmpty()) {
            Utils.toast(this, "Password is empty");
            return false;
        }
        return true;
    }
}
