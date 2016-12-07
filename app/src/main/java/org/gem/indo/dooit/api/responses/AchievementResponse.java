package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.Badge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/25.
 */

public class AchievementResponse {
    @SerializedName("weekly_streak")
    private int weeklyStreak;
    private List<Badge> badges = new ArrayList<>();

    public int getWeeklyStreak() {
        return weeklyStreak;
    }

    public void setWeeklyStreak(int weeklyStreak) {
        this.weeklyStreak = weeklyStreak;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }
}
