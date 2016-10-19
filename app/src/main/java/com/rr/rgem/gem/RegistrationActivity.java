package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rr.rgem.gem.service.AuthService;
import com.rr.rgem.gem.service.ErrorUtil;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.service.errors.RegistrationError;
import com.rr.rgem.gem.service.model.AuthLogin;
import com.rr.rgem.gem.service.model.AuthToken;
import com.rr.rgem.gem.service.model.Profile;
import com.rr.rgem.gem.service.model.RegistrationResponse;
import com.rr.rgem.gem.service.model.User;
import com.rr.rgem.gem.views.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chris on 9/8/2016.
 */
public class RegistrationActivity extends ApplicationActivity {

    private static final String TAG = "RegistrationActivity";

    AuthService authService;
    ErrorUtil errorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_page);

        final Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));

        if (persisted.isRegistered()) {
            Intent intent = new Intent(RegistrationActivity.this, RegistrationCompleteActivity.class);
            RegistrationActivity.this.startActivity(intent);
            RegistrationActivity.this.finish();
            return;
        }

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        authService = factory.createAuthService();
        errorUtil = new ErrorUtil(factory.createRetrofit());

        final Button button = (Button) findViewById(R.id.buttonRegister);
        button.setOnClickListener(new View.OnClickListener() {

            private boolean isValidMobile(CharSequence phone){
                return android.util.Patterns.PHONE.matcher(phone).matches();
            }

            private void toast(CharSequence text){
                Context context = getApplicationContext();
                Utils.toast(context,text);

            }

            public void onClick(View v) {
                TextView editName = (TextView)findViewById(R.id.editTextName);
                TextView editMobile = (TextView)findViewById(R.id.editTextMobile);
                TextView editPassword = (TextView)findViewById(R.id.editTextPassword);

                if(editName.getText().length() < 4){
                    toast(getString(R.string.invalid_username_length));
                    return;
                }

                if(!isValidMobile(editMobile.getText())){
                    toast(getString(R.string.invalid_mobile_number));
                    return;
                }

                if(editPassword.getText().length() < 8){
                    toast(getString(R.string.invalid_password_length));
                    return;
                }

                //TODO: check for log in activity

                final User user = new User();
                user.setUsername(editName.getText().toString());
                user.setPassword(editPassword.getText().toString());

                Profile profile = new Profile();
                profile.setMobile(editMobile.getText().toString());

                user.setProfile(profile);

                if (RegistrationActivity.this.authService == null) {
                    throw new NullPointerException("Auth Service is null");
                }

                RegistrationActivity.this.authService.register(user).enqueue(new Callback<RegistrationResponse>() {
                    @Override
                    public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(RegistrationActivity.TAG, "Registration successful");
                            Utils.toast(getApplicationContext(), getString(R.string.congratulations_registered));
                            persisted.setRegistered(true);
                            // TODO: Should password be cleared before save?
                            persisted.saveUser(user);
                            retrieveToken(user);
                        } else {
                            RegistrationError error = RegistrationActivity.this.errorUtil
                                    .parseError(response, RegistrationError.class);
                            Log.d(RegistrationActivity.TAG, "Registration failed " + error.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                        Log.d(RegistrationActivity.TAG, "Registration error", t);
                    }
                });
            }
        });
    }

    void startNextActivity() {
        Intent intent = new Intent(this, RegistrationCompleteActivity.class);
        RegistrationActivity.this.startActivity(intent);
        RegistrationActivity.this.finish();
    }

    void retrieveToken(User user) {
        // TODO: Log user in immediately after successful registration
        Persisted persisted = new Persisted(this);
        final AuthLogin login = AuthLogin.fromUser(user);

        authService.createToken(login).enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if (response.isSuccessful()) {
                    RegistrationActivity.this.startNextActivity();
                } else {

                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {

            }
        });
    }
}
