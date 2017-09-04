package org.gem.indo.dooit.models.enums;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public enum BotType {
    DEFAULT(1),
    RETURNING_USER(2),

    // Challenges
    CHALLENGE_PARTICIPANT_BADGE(100),
    CHALLENGE_WINNER(101),

    // Goals
    GOAL_ADD(200),
    GOAL_EDIT(201),
    GOAL_DEPOSIT(202),
    GOAL_WITHDRAW(203),

    // Surveys
    SURVEY_ADHOC(300),
    SURVEY_BASELINE(301),
    SURVEY_EATOOL(302),
    SURVEY_ENDLINE(303),

    // Budget
    BUDGET_CREATE(400),
    BUDGET_EDIT(401);

    private static Map<Integer, BotType> map = new LinkedHashMap<>();

    static {
        for (BotType type : BotType.values())
            map.put(type.id, type);
    }

    int id;

    BotType(int id) {
        this.id = id;
    }

    ////////
    // ID //
    ////////

    public int getId() {
        return id;
    }

    public static BotType byId(int id) {
        return map.get(id);
    }
}
