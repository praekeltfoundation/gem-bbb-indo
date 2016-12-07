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
    GOAL_END_DATE("goal.end_date");

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
