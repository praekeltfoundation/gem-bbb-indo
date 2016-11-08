package com.nike.dooit.views.main.fragments.tip.providers;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.models.Tip;

import java.util.List;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/08.
 */

public class FavouriteTips implements TipProvider {

    TipManager tipManager;

    public FavouriteTips(TipManager tipManager) {
        this.tipManager = tipManager;
    }

    @Override
    public Observable<List<Tip>> retrieveTips(DooitErrorHandler errorHandler) {
        return tipManager.retrieveFavourites(errorHandler);
    }
}
