package com.nike.dooit.api.managers;

import android.app.Application;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.interfaces.TipAPI;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.models.Tip;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public class TipManager extends DooitManager {

    private final TipAPI tipAPI;

    @Inject
    public TipManager(Application application) {
        super(application);
        tipAPI = retrofit.create(TipAPI.class);
    }

    public Observable<List<Tip>> retrieveTips(DooitErrorHandler errorHandler) {
        return useNetwork(tipAPI.getTips(), errorHandler);
    }

    public Observable<List<Tip>> retrieveFavourites(DooitErrorHandler errorHandler) {
        return useNetwork(tipAPI.getFavourites(), errorHandler);
    }

    public Observable<EmptyResponse> favourite(int id, DooitErrorHandler errorHandler) {
        return useNetwork(tipAPI.favourite(id), errorHandler);
    }

    public Observable<EmptyResponse> unfavourite(int id, DooitErrorHandler errorHandler) {
        return useNetwork(tipAPI.unfavourite(id), errorHandler);
    }
}
