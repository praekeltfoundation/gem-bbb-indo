package com.nike.dooit.views.main.fragments.tip.providers;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.Tip;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/08.
 */

public abstract class TipProvider {

    @Inject
    TipManager tipManager;

    @Inject
    Persisted persisted;

    public TipProvider(DooitApplication application) {
        application.component.inject(this);
    }

    abstract public Observable<List<Tip>> retrieveTips(DooitErrorHandler errorHandler);
    abstract public List<Tip> loadTips();
    abstract public void saveTips(List<Tip> tips);
    abstract public boolean hasTips();
    abstract public void clearTips();
}
