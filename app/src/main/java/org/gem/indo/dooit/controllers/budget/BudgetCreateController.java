package org.gem.indo.dooit.controllers.budget;

import android.content.Context;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/03/01.
 */

public class BudgetCreateController extends DooitBotController {

    private Budget budget;

    public BudgetCreateController(Context context, @Nullable Budget budget) {
        super(context, BotType.BUDGET_CREATE);

        // A new Budget can be persisted if the conversation is reloaded.
        if (budget == null)
            this.budget = new Budget();
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();
        switch(paramType) {
            case BUDGET_DEFAULT_SAVINGS:
                if (budget != null)
                    model.values.put(key, budget.getDefaultSavings());
                break;
            case BUDGET_DEFAULT_SAVINGS_FORMATTED:
                if (budget != null)
                    model.values.put(key, CurrencyHelper.format(budget.getDefaultSavings()));
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }
}
