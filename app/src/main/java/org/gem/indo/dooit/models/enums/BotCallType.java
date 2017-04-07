package org.gem.indo.dooit.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2016/12/08.
 */

public enum BotCallType {

    @SerializedName("#do_create")
    DO_CREATE,

    @SerializedName("#do_update")
    DO_UPDATE,

    @SerializedName("#do_delete")
    DO_DELETE,

    @SerializedName("#do_deposit")
    DO_DEPOSIT,

    @SerializedName("#do_withdraw")
    DO_WITHDRAW,

    @SerializedName("#add_badge")
    ADD_BADGE,

    @SerializedName("#winning_badge")
    WINNING_BADGE,

    @SerializedName("#populate_goal")
    POPULATE_GOAL,

    @SerializedName("#confirm_notify")
    CONFIRM_NOTIFY,

    @SerializedName("#update_local_goal")
    UPDATE_LOCAL_GOAL,

    @SerializedName("#update_goal_confirm")
    UPDATE_GOAL_CONFIRM,

    @SerializedName("#participant_badge")
    PARTICIPANT_BADGE,

    @SerializedName("#set_target_to_default")
    SET_TARGET_TO_DEFAULT,

    @SerializedName("#set_tip_query")
    SET_TIP_QUERY,

    @SerializedName("#add_expense")
    ADD_EXPENSE,

    @SerializedName("#clear_expenses_state")
    CLEAR_EXPENSES_STATE,

    @SerializedName("#clear_expenses")
    CLEAR_EXPENSES,

    @SerializedName("#branch_budget_final")
    BRANCH_BUDGET_FINAL,

    @SerializedName("#branch_budget_goals")
    BRANCH_BUDGET_GOALS,

    @SerializedName("#update_budget_income")
    UPDATE_BUDGET_INCOME,

    @SerializedName("#update_single_expense")
    UPDATE_SINGLE_EXPENSE,

    @SerializedName("#validate_budget_savings")
    VALIDATE_BUDGET_SAVINGS,

    @SerializedName("#update_budget_savings")
    UPDATE_BUDGET_SAVINGS,

    @SerializedName("#list_expense_quick_answers")
    LIST_EXPENSE_QUICK_ANSWERS,

    @SerializedName("#list_expense_categories_unselected")
    LIST_EXPENSE_CATEGORIES_UNSELECTED,

    @SerializedName("#update_budget_single_expense")
    UPDATE_BUDGET_SINGLE_EXPENSE,

    @SerializedName("#delete_expense")
    DELETE_BUDGET_EXPENSE,

    @SerializedName("#list_goals")
    LIST_GOALS
}
