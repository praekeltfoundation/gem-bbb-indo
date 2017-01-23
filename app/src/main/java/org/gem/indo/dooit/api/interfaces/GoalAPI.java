package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.api.responses.TransactionResponse;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.models.goal.GoalTransaction;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by herman on 2016/11/12.
 */

public interface GoalAPI {

    @GET("/api/goals/")
    Observable<List<Goal>> getGoals();

    @POST("/api/goals/")
    Observable<Goal> createGoal(@Body Goal goal);

    @POST("/api/goals/{id}/transactions/")
    Observable<TransactionResponse> createGoalTransactions(@Path("id") long id,
                                                           @Body List<GoalTransaction> transactions);

    @GET("/api/goal-prototypes/")
    Observable<List<GoalPrototype>> getGoalPrototypes();

    @PUT("/api/goals/{id}/")
    Observable<Goal> updateGoal(@Path("id") long goalId, @Body Goal goal);

    @DELETE("/api/goals/{id}/")
    Observable<EmptyResponse> deleteGoal(@Path("id") long goalId);
}
