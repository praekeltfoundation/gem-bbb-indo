package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2016/11/25.
 */

public class AchievementResponse {
    @SerializedName("weekly_streak")
    private int weeklyStreak;

    public int getWeeklyStreak() {
        return weeklyStreak;
    }

    public void setWeeklyStreak(int weeklyStreak) {
        this.weeklyStreak = weeklyStreak;
    }
}
