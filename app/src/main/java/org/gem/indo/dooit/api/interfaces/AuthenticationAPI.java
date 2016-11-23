package org.gem.indo.dooit.api.interfaces;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


import org.gem.indo.dooit.api.requests.LoginRequest;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.api.responses.OnboardingResponse;
import org.gem.indo.dooit.models.User;

/**
 * Created by herman on 2016/11/05.
 */

public interface AuthenticationAPI {

    @POST("/api/token/")
    Observable<AuthenticationResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/users/")
    Observable<OnboardingResponse> register(@Body User user);

}
