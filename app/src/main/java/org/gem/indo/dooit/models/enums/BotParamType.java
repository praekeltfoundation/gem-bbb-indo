package org.gem.indo.dooit.models.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public enum BotParamType {
    // User
    USER_USERNAME("user.username"),
    USER_GOALS("user.goals"),

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

    // Goal Prototype
    GOAL_PROTO("goal_proto"),
    GOAL_PROTO_ID("goal_proto.id"),
    GOAL_PROTO_NAME("goal_proto.name"),
    GOAL_PROTO_IMAGE_URL("goal_proto.image_url"),

    // Challenge
    CHALLENGE_ID("challenge.id"),
    CHALLENGE_TITLE("challenge.title"),
    CHALLENGE_SUBTITLE("challenge.subtitle"),
    CHALLENGE_IMAGE_URL("challenge.image_url"),
    CHALLENGE_INTRO("challenge.intro"),
    CHALLENGE_OUTRO("challenge.outro"),

    // Tips
    TIP_INTRO("tip.intro"),
    TIP_TITLE("tip.title"),
    TIP_IMAGE_URL("tip.image_url"),
    TIP_ARTICLE_URL("tip.article_url"),

    // Badge
    BADGE_NAME("badge.name"),
    BADGE_IMAGE_URL("badge.image_url"),
    BADGE_SOCIAL_URL("badge.social_url"),
    BADGE_EARNED_ON("badge.earned_on"),

    // Survey
    SURVEY_TITLE("survey.title"),
    SURVEY_INTRO("survey.intro"),
    SURVEY_OUTRO("survey.outro");

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
