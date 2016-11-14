package com.nike.dooit.api.interfaces;

import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.models.Tip;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface TipAPI {

    @GET("/api/tips/")
    Observable<List<Tip>> getTips();

    @GET("/api/tips/favourites/")
    Observable<List<Tip>> getFavourites();

    @POST("/api/tips/{id}/favourite/")
    Observable<EmptyResponse> favourite(@Path("id") int id);

    @POST("/api/tips/{id}/unfavourite/")
    Observable<EmptyResponse> unfavourite(@Path("id") int id);
}
