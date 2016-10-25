package com.rr.rgem.gem;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ApplicationActivity {

    private final static String TAG = "LoginActivity";

    AuthService authService;
    ImageDownloader imageDownloader;
    ErrorUtil errorUtil;
    Persisted persist;
    Handler handler;
    TextView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        handler = new Handler(getMainLooper());

        // Setup ui
        progressView = (TextView) findViewById(R.id.textViewProgress);
        progressView.setText(getString(R.string.login_progress));
        progressView.setVisibility(View.GONE);
        setInputEnabled(true);

        // Setup REST services
        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        authService = factory.createAuthService();
        imageDownloader = factory.createImageDownloader();
        persist = new Persisted(this);
        errorUtil = new ErrorUtil(factory.createRetrofit());

        User user = persist.loadUser();
        if (user.getUsername() != null) {
            EditText editMobile = (EditText) findViewById(R.id.editTextMobile);
            editMobile.setText(user.getUsername());
        }

        Button button = (Button) findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast(LoginActivity.this, "Login clicked");

                progressView.setText(R.string.login_progress);
                progressView.setVisibility(View.VISIBLE);
                setInputEnabled(false);

                TextView editMobile = (TextView) LoginActivity.this.findViewById(R.id.editTextMobile);
                TextView editPassword = (TextView) LoginActivity.this.findViewById(R.id.editTextPassword);
                String mobile = editMobile.getText().toString();
                String password = editPassword.getText().toString();

                if (!mobileValid(mobile) || !passwordValid(password)) {
                    return;
                }

                retrieveToken(mobile, password);
            }
        });
    }

    void retrieveToken(String mobile, String password) {
        AuthLogin login = new AuthLogin(mobile, password);
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
        returnToActivity();
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

    void setInputEnabled(boolean enabled) {
        View editMobile = findViewById(R.id.editTextMobile);
        View editPassword = findViewById(R.id.editTextPassword);
        View buttonLogin = findViewById(R.id.buttonLogin);

        editMobile.setEnabled(enabled);
        editPassword.setEnabled(enabled);
        buttonLogin.setEnabled(enabled);
    }

    void returnToActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginActivity.this.finish();
            }
        }, 2000);
    }
}
