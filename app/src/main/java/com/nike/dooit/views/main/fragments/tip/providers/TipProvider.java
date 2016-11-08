package com.nike.dooit.views.main.fragments.tip.providers;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.models.Tip;

import java.util.List;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/08.
 */

public interface TipProvider {
    Observable<List<Tip>> retrieveTips(DooitErrorHandler errorHandler);
}
