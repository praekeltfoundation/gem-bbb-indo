package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.GoalAPI;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.models.GoalTransaction;

import java.util.ArrayList;
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

    public Observable<Goal> createGoal(Goal goal, DooitErrorHandler errorHandler) {
        return useNetwork(goalAPI.createGoal(goal), errorHandler);
    }

    public Observable<EmptyResponse> addGoalTransaction(Goal goal, GoalTransaction transaction,
                                                        DooitErrorHandler errorHandler) {
        List<GoalTransaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        return useNetwork(goalAPI.createGoalTransactions(goal.getId(), transactions), errorHandler);
    }

    public Observable<List<GoalPrototype>> retrieveGoalPrototypes(DooitErrorHandler errorHandler) {
        return useNetwork(goalAPI.getGoalPrototypes(), errorHandler);
    }

    public Observable<Goal> updateGoal(Goal goal, DooitErrorHandler errorHandler) {
        return useNetwork(goalAPI.updateGoal(goal.getId(), goal), errorHandler);
    }

    public Observable<EmptyResponse> deleteGoal(Goal goal, DooitErrorHandler errorHandler) {
        return useNetwork(goalAPI.deleteGoal(goal.getId()), errorHandler);
    }
}
