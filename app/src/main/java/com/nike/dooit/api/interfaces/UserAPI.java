package com.nike.dooit.api.interfaces;

import com.nike.dooit.api.requests.ChangeUser;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.models.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface UserAPI {

    @GET("/api/users/{id}/")
    Observable<User> getUser(@Path("id") int id);
    @PATCH("/api/users/{id}/")
    Observable<EmptyResponse> renameUser(@Path("id") long id, @Body ChangeUser name);

}
