package org.gem.indo.dooit.controllers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public enum BotParamType {
    // User
    USER_USERNAME("user.username"),

    // Goal
    GOAL_NAME("goal.name"),
    GOAL_VALUE("goal.value"),
    GOAL_TARGET("goal.target"),
    GOAL_TARGET_CURRENCY("goal.target_currency"),
    GOAL_END_DATE("goal.end_date"),
    GOAL_WEEKLY_TARGET("goal.weekly_target"),
    GOAL_WEEKLY_TARGET_CURRENCY("goal.weekly_target_currency"),
    GOAL_WEEKS_LEFT_UP("goal.weeks_left_up"),
    GOAL_WEEKS_LEFT_DOWN("goal.weeks_left_down"),
    GOAL_REMAINDER_DAYS_LEFT("goal.remainder_days_left"),
    GOAL_IMAGE_URL("goal.image_url"),
    GOAL_LOCAL_IMAGE_URI("goal.local_image_uri"),
    GOAL_HAS_LOCAL_IMAGE_URI("goal.has_local_image_uri"),

    // Tips
    TIP_INTRO("tip.intro");

    private static Map<String, BotParamType> map = new HashMap<>();

    static {
        for (BotParamType paramType : BotParamType.values())
            map.put(paramType.getKey(), paramType);
    }

    /**
     * The parameter key used in strings.
     */
    private String key;

    BotParamType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static BotParamType byKey(String key) {
        return map.get(key);
    }
}