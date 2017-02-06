package org.gem.indo.dooit.helpers;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.responses.AchievementResponse;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.goal.Goal;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Helper to build a map of parameters which can be used when processing a
 * {@link org.gem.indo.dooit.helpers.bot.param.ParamMatch}.
 *
 * The params are application specific.
 *
 * Created by Wimpie Victor on 2017/01/16.
 */

public class DooitParamBuilder {

    @Inject
    Persisted persisted;

    private AchievementResponse achievements;
    private User user;
    private Goal goal;

    private DooitParamBuilder(Context context) {
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
    }

    public static DooitParamBuilder create(Context context) {
        return new DooitParamBuilder(context);
    }

    public DooitParamBuilder setAchievements(AchievementResponse response) {
        this.achievements = response;
        return this;
    }

    public DooitParamBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public DooitParamBuilder setGoal(Goal goal) {
        this.goal = goal;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> map = new HashMap<>();

        // Achievement Stats
        if (achievements != null) {
            map.put(BotParamType.ACHIEVEMENTS_WEEKLY_STREAK.getKey(), achievements.getWeeklyStreak());
            map.put(BotParamType.ACHIEVEMENTS_LAST_SAVING_DATETIME.getKey(), achievements.getLastSavingDateTime());
            map.put(BotParamType.ACHIEVEMENTS_WEEKS_SINCE_SAVED.getKey(), achievements.getWeeksSinceSaved());
        }

        // User

        // If no user is set on builder, use the persisted user. Persisted can still return a null
        // user.
        if (user == null)
            user = persisted.getCurrentUser();

        if (user != null) {
            map.put(BotParamType.USER_USERNAME.getKey(), user.getUsername());
            map.put(BotParamType.USER_FIRST_NAME.getKey(), user.getFirstName());
            map.put(BotParamType.USER_PREFERRED_NAME.getKey(), user.getPreferredName());
        }

        return map;
    }
}
