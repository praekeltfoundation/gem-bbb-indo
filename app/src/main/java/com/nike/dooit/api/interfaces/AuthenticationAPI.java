package com.nike.dooit.api.interfaces;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


import com.nike.dooit.api.requests.LoginRequest;
import com.nike.dooit.api.responses.AuthenticationResponse;
import com.nike.dooit.api.responses.OnboardingResponse;
import com.nike.dooit.models.User;

/**
 * Created by herman on 2016/11/05.
 */

public interface AuthenticationAPI {

    @POST("/api/token/")
    Observable<AuthenticationResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/users/")
    Observable<OnboardingResponse> register(@Body User user);

}
