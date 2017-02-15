package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.goal.Goal;

/**
 * Created by Chad Garrett on 2017/02/14.
 */

public class GoalResponse {

    @SerializedName("available")
    private boolean available;

    @SerializedName("overdue_goal")
    private Goal goal;

    public boolean isThereAnOverdueGoal() { return available; }

    public Goal getGoal() {
        return goal;
    }

    public String getGoalName() { return goal.getName(); }

    public boolean isGoalOverdue() {
        return goal == null;
    }
}
