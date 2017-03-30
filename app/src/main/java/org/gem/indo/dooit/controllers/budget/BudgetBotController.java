package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/03/30.
 */

public abstract class BudgetBotController extends DooitBotController {

    protected static String INCOME_INPUT = "income_amount";

    protected BotRunner botRunner;
    protected MainActivity activity;
    protected Budget budget;

    public BudgetBotController(Context context, BotType botType,
                               @NonNull Activity activity, @NonNull BotRunner botRunner) {
        super(context, botType);

        this.activity = (MainActivity) activity;
        this.botRunner = botRunner;
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case BUDGET:
                return budget;
            default:
                return super.getObject(objType);
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case SET_TIP_QUERY:
                tipQuery();
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    private void tipQuery() {
        if (activity == null)
            return;
        activity.setTipQuery(activity.getString(R.string.budget_create_qry_tip_income));
    }

    ////////////////
    // Validation //
    ////////////////


    @Override
    public boolean validate(String name, String input) {
        switch (name) {
            case "income_amount":
                return validateIncome(input);
            case "savings_amount":
                return validateSavings(input);
            default:
                return super.validate(name, input);
        }
    }

    boolean validateIncome(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_income__empty);
            return false;
        }
        try {
            double income = Double.parseDouble(input);
            if (income <= 0.0) {
                toast(R.string.budget_create_err_income__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast("Invalid number");
            return false;
        }
    }

    boolean validateSavings(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_savings__empty);
            return false;
        }
        try {
            double value = Double.parseDouble(input);

            // Find income defined earlier
            Map<String, Answer> answerLog = botRunner.getAnswerLog();
            Double income = null;
            if (answerLog.containsKey(INCOME_INPUT))
                income = Double.parseDouble(answerLog.get(INCOME_INPUT).getValue());

            if (value <= 0.0) {
                toast(R.string.budget_create_err_savings__zero);
                return false;
            } else if (income != null && value > income) {
                toast(R.string.budget_create_err_savings__gt_income);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.budget_create_err__invalid_number);
            return false;
        }
    }

    boolean validateExpense(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_expense__empty);
            return false;
        }
        try {
            double expense = Double.parseDouble(input);

            if (expense <= 0) {
                toast(R.string.budget_create_err_expense__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.budget_create_err__invalid_number);
            return false;
        }
    }
}
