package org.gem.indo.dooit.controllers.budget;

import android.content.Context;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/03/01.
 */

public class BudgetCreateController extends DooitBotController {

    private Budget budget;

    public BudgetCreateController(Context context, @Nullable Budget budget) {
        super(context, BotType.BUDGET_CREATE);
        if (budget == null)
            this.budget = budget;
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }
}
