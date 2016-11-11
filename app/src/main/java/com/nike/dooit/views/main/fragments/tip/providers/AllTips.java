package com.nike.dooit.views.main.fragments.tip.providers;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.models.Tip;

import java.util.List;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/08.
 */

public class AllTips extends TipProvider {

    public AllTips(DooitApplication application) {
        super(application);
    }

    @Override
    public Observable<List<Tip>> retrieveTips(DooitErrorHandler errorHandler) {
        return tipManager.retrieveTips(errorHandler);
    }

    @Override
    public List<Tip> loadTips() {
        return persisted.getTips();
    }

    @Override
    public void saveTips(List<Tip> tips) {
        persisted.setTips(tips);
    }

    @Override
    public boolean hasTips() {
        return persisted.hasTips();
    }

    @Override
    public void clearTips() {
        persisted.clearTips();
    }
}
