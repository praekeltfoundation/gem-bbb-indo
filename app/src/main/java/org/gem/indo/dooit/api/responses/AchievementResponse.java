package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.Badge;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

// TODO: Consider moving this class to `models` if it is used for anything besides HTTP requests

/**
 * Created by Wimpie Victor on 2016/11/25.
 */

public class AchievementResponse {

    // TODO: Weeks by which the users should be notified are to be decided
    public static final int WEEKS_SINCE_THRESHOLD = 2;

    @SerializedName("weekly_streak")
    private int weeklyStreak;

    /**
     * The date time of the user's last deposit transaction.
     */
    @SerializedName("last_saving_datetime")
    private DateTime lastSavingDateTime;

    /**
     * The number of weeks since the user last saved
     */
    @SerializedName("weeks_since_saved")
    private int weeksSinceSaved;

    /**
     * All Badges that the user has earned
     */
    private List<Badge> badges = new ArrayList<>();

    /////////////
    // Getters //
    /////////////

    public int getWeeklyStreak() {
        return weeklyStreak;
    }

    public DateTime getLastSavingDateTime() {
        return lastSavingDateTime;
    }

    public int getWeeksSinceSaved() {
        return weeksSinceSaved;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    /////////////
    // Methods //
    /////////////

    public boolean shouldRemindSavings() {
        return weeksSinceSaved != 0 // Has saved this week
                && weeksSinceSaved % WEEKS_SINCE_THRESHOLD == 0;
    }
}
