package com.nike.dooit.views.main.fragments.tip.providers;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.models.Tip;

import java.util.List;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/08.
 */

public class FavouriteTips extends TipProvider {

    public FavouriteTips(DooitApplication application) {
        super(application);
    }

    @Override
    public Observable<List<Tip>> retrieveTips(DooitErrorHandler errorHandler) {
        return tipManager.retrieveFavourites(errorHandler);
    }

    @Override
    public List<Tip> loadTips() {
        return persisted.getFavouriteTips();
    }

    @Override
    public void saveTips(List<Tip> tips) {
        persisted.setFavourites(tips);
    }

    @Override
    public boolean hasTips() {
        return persisted.hasFavourites();
    }

    @Override
    public void clearTips() {
        persisted.clearFavourites();
    }
}
