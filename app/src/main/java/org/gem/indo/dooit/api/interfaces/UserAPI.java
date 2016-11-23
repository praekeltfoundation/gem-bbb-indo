package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.models.User;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface UserAPI {

    @GET("/api/users/{id}/")
    Observable<User> getUser(@Path("id") int id);

}
