package org.gem.indo.dooit.controllers.budget;

import android.content.Context;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/03/01.
 */

public class BudgetCreateController extends DooitBotController {

    public BudgetCreateController(Context context) {
        super(context, BotType.BUDGET_CREATE);
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }
}
