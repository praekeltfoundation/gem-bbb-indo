package com.nike.dooit.api.managers;

import android.app.Application;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.interfaces.AuthenticationAPI;
import com.nike.dooit.api.requests.LoginRequest;
import com.nike.dooit.api.responses.AuthenticationResponse;
import com.nike.dooit.api.responses.OnboardingResponse;
import com.nike.dooit.models.User;

import javax.inject.Inject;

import okhttp3.Request;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public class AuthenticationManager extends DooitManager {

    private final AuthenticationAPI authenticationAPI;

    @Inject
    public AuthenticationManager(Application application) {
        super(application);
        authenticationAPI = retrofit.create(AuthenticationAPI.class);
    }

    @Override
    protected Request.Builder addTokenToRequest(Request.Builder requestBuilder) {
        return requestBuilder;
    }

    public Observable<AuthenticationResponse> login(String username, String password, DooitErrorHandler errorHandler) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return useNetwork(authenticationAPI.login(loginRequest), errorHandler);
    }

    public Observable<OnboardingResponse> onboard(User user, DooitErrorHandler errorHandler) {
        return useNetwork(authenticationAPI.register(user), errorHandler);
    }


}