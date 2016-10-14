package com.rr.rgem.gem.service;

import com.firebase.client.core.Repo;
import com.rr.rgem.gem.service.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by chris on 10/12/2016.
 */
public interface CMS {
    @GET("users/{user}")
    Call<List<User>> getUser(@Path("user") String user);
    @POST("users/{user}")
    Call<User> addUser(@Path("user") String user,@Body User data);
}
