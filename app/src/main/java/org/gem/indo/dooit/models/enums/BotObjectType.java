package org.gem.indo.dooit.models.enums;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public enum BotObjectType {
    CHALLENGE(100),
    BADGE(200),
    GOAL(300),
    GOALS(400),
    GOAL_PROTOTYPES(500),
    TIP(600),
    SURVEY(700),
    EXPENSE_CATEGORIES(800);

    private static Map<Integer, BotObjectType> map = new LinkedHashMap<>();

    static {
        for (BotObjectType type : BotObjectType.values())
            map.put(type.id, type);
    }

    int id;

    BotObjectType(int id) {
        this.id = id;
    }

    ////////
    // ID //
    ////////


    public int getId() {
        return id;
    }

    public static BotObjectType byId(int id) {
        return map.get(id);
    }
}
