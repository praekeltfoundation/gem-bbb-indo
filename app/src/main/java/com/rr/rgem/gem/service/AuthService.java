package com.rr.rgem.gem.service;

import com.rr.rgem.gem.service.model.AuthLogin;
import com.rr.rgem.gem.service.model.AuthToken;
import com.rr.rgem.gem.service.model.AuthTokenResponse;
import com.rr.rgem.gem.service.model.RegistrationResponse;
import com.rr.rgem.gem.service.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public interface AuthService {

    @POST("/api/token/")
    Call<AuthTokenResponse> createToken(@Body AuthLogin login);

    @POST("/api/users/")
    Call<RegistrationResponse> register(@Body User user);
}
