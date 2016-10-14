package com.rr.rgem.gem.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class CMSFactory {
    static public CMS create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(CMS.class);
    }
}
