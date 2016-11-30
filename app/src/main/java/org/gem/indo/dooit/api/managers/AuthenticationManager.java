package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.AuthenticationAPI;
import org.gem.indo.dooit.api.requests.LoginRequest;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.api.responses.OnboardingResponse;
import org.gem.indo.dooit.models.User;

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
        super(application,true);
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