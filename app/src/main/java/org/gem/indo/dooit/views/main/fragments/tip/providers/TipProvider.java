package org.gem.indo.dooit.views.main.fragments.tip.providers;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Tip;

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
