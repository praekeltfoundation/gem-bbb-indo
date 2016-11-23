package com.nike.dooit.api.interfaces;

import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.GoalTransaction;
import com.nike.dooit.models.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Observable<EmptyResponse> createGoalTransactions(@Path("id") long id,
                                                     @Body List<GoalTransaction> transactions);
}
