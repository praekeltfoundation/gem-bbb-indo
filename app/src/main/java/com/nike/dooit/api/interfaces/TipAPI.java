package com.nike.dooit.api.interfaces;

import com.nike.dooit.models.Tip;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface TipAPI {

    @GET("/api/tips/")
    Observable<List<Tip>> getTips();

    @GET("/api/tips/favourites/")
    Observable<List<Tip>> getFavourites();

}
