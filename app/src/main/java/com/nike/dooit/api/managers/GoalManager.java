package com.nike.dooit.api.managers;

import android.app.Application;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.interfaces.GoalAPI;
import com.nike.dooit.models.Goal;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by herman on 2016/11/12.
 */

public class GoalManager extends DooitManager {

    private final GoalAPI goalAPI;

    @Inject
    public GoalManager(Application application) {
        super(application);
        goalAPI = retrofit.create(GoalAPI.class);
    }

    public Observable<List<Goal>> retrieveGoals(DooitErrorHandler errorHandler) {
        return useNetwork(goalAPI.getGoals(), errorHandler);
    }
}
