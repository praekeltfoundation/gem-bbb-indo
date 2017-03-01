package org.gem.indo.dooit.models.enums;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public enum BotType {
    RETURNING_USER,
    CHALLENGE_WINNER,
    CHALLENGE_PARTICIPANT_BADGE,

    // Goals
    DEFAULT,
    GOAL_ADD,
    GOAL_DEPOSIT,
    GOAL_WITHDRAW,
    GOAL_EDIT,

    // FIXME: Tip intro is now unused and should be removed
    TIP_INTRO,

    // Surveys
    SURVEY_BASELINE,
    SURVEY_EATOOL,
    SURVEY_ADHOC,

    // Budget
    BUDGET_CREATE,
    BUDGET_EDIT
}
