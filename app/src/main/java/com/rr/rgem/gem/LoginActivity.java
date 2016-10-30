package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rr.rgem.gem.image.ImageCallback;
import com.rr.rgem.gem.image.ImageDownloader;
import com.rr.rgem.gem.image.ImageStorage;
import com.rr.rgem.gem.service.AuthService;
import com.rr.rgem.gem.service.ErrorUtil;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.service.errors.AuthError;
import com.rr.rgem.gem.service.model.AuthLogin;
import com.rr.rgem.gem.service.model.AuthTokenResponse;
import com.rr.rgem.gem.service.model.User;
import com.rr.rgem.gem.views.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ApplicationActivity {

    private final static String TAG = "LoginActivity";

    @BindView(R.id.textViewProgress) TextView progressView;
    @BindView(R.id.textViewUsername) TextInputLayout textViewUsername;
    @BindView(R.id.editTextUsername) TextInputEditText editUsername;
    @BindView(R.id.textViewPassword) TextInputLayout textViewPassword;
    @BindView(R.id.editTextPassword) TextInputEditText editPassword;
    @BindView(R.id.buttonLogin) Button buttonLogin;

    AuthService authService;
    ImageDownloader imageDownloader;
    ErrorUtil errorUtil;
    Persisted persist;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);
        ButterKnife.bind(this);

        editUsername.addTextChangedListener(new ErrorResetWatcher(textViewUsername));
        editPassword.addTextChangedListener(new ErrorResetWatcher(textViewPassword));

        handler = new Handler(getMainLooper());

        // Setup REST services
        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        authService = factory.createAuthService();
        imageDownloader = factory.createImageDownloader();
        persist = new Persisted(this);
        errorUtil = new ErrorUtil(factory.createRetrofit());

        // Setup ui
        progressView.setText(getString(R.string.login_progress));
        progressView.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textViewDebugUrl))
                .setText(String.format(getString(R.string.login_debug_url), factory.getBaseUrl()));
        setInputEnabled(true);

        User user = persist.loadUser();
        if (user.getUsername() != null) {
            editUsername.setText(user.getUsername());
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast(LoginActivity.this, "Login clicked");

                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                if (!usernameValid(username) || !passwordValid(password)) {
                    return;
                }

                progressView.setText(R.string.login_progress);
                progressView.setVisibility(View.VISIBLE);
                setInputEnabled(false);

                retrieveToken(username, password);
            }
        });
    }

    void retrieveToken(String username, String password) {
        AuthLogin login = new AuthLogin(username, password);
        authService.createToken(login).enqueue(new Callback<AuthTokenResponse>() {
            @Override
            public void onResponse(Call<AuthTokenResponse> call, Response<AuthTokenResponse> response) {
                Utils.toast(LoginActivity.this, "Response");
                Log.d(LoginActivity.TAG, String.format("Login Status %d", response.code()));
                if (response.isSuccessful()) {
                    Log.d(LoginActivity.TAG, "Login Successful " + response.body().toString());
                    progressView.setText(R.string.login_success);

                    AuthTokenResponse auth = response.body();
                    LoginActivity.this.persist.saveToken(auth.getToken());
                    LoginActivity.this.persist.saveUser(auth.getUser());
                    LoginActivity.this.persist.setLoggedIn(true);

                    retrieveProfileImage(auth.getUser());
                } else {
                    ErrorUtil.WebServiceError error = errorUtil.parseError(response, AuthError.class);
                    Log.d(LoginActivity.TAG, "API Errors: " + error);
                    progressView.setText(R.string.login_failed);
                    Utils.toast(LoginActivity.this, error.getNonFieldErrorsJoined());
                    setInputEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<AuthTokenResponse> call, Throwable t) {
                Log.e(LoginActivity.TAG, "Create Token HTTP Failure", t);
                progressView.setText(R.string.login_failed);
                setInputEnabled(true);
            }
        });
    }

    void retrieveProfileImage(User user) {
        Log.d(TAG, "Retrieving Profile Image");
        progressView.setText(R.string.profile_image_progress);

        String url = String.format("/api/profile-image/%d/", user.getId());
        imageDownloader.retrieveImage(url, new ImageCallback() {
            @Override
            public void onLoad(Bitmap image) {
                Log.d(TAG, "Retrieve profile success");
                progressView.setText(R.string.profile_image_success);

                ImageStorage storage = new ImageStorage(LoginActivity.this, "imageDir");
                storage.saveImage("profile.jpg", image);
                completeLogin();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "User has no profile image.");
                progressView.setText(R.string.profile_image_failed);

                BitmapDrawable drawable = (BitmapDrawable) ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_launcher, null);
                Bitmap image = drawable.getBitmap();

                ImageStorage storage = new ImageStorage(LoginActivity.this, "imageDir");
                storage.saveImage("profile.jpg", image);

                completeLogin();
            }
        });
    }

    void completeLogin() {
        persist.setLoggedIn(true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    boolean usernameValid(String username) {
        if (username == null || username.isEmpty()) {
            textViewUsername.setError("Username is empty");
            return false;
        }
        return true;
    }

    boolean passwordValid(String password) {
        if (password == null || password.isEmpty()) {
            textViewPassword.setError("Password is empty");
            return false;
        }
        return true;
    }

    void setInputEnabled(boolean enabled) {
        editUsername.setEnabled(enabled);
        editPassword.setEnabled(enabled);
        buttonLogin.setEnabled(enabled);
    }

    private static class ErrorResetWatcher implements TextWatcher {

        TextInputLayout view;

        public ErrorResetWatcher(TextInputLayout view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            view.setError("");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }
}
