package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.goal.Goal;

/**
 * Created by Chad Garrett on 2017/02/14.
 */

public class GoalResponse {

    @SerializedName("overdue_goals")
    private Goal goal;

    public Goal getGoal() {
        return goal;
    }

    public boolean isGoalOverdue() {
        return goal == null;
    }
}
