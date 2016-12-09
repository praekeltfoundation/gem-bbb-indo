package org.gem.indo.dooit.controllers.misc;

import android.content.Context;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/09.
 */

public class ReturningUserController extends DooitBotController {

    private BotAdapter botAdapter;
    private List<Goal> goals;
    private Tip tip;

    public ReturningUserController(Context context, BotAdapter botAdapter, List<Goal> goals, Tip tip) {
        super(context, BotType.RETURNING_USER);
        this.botAdapter = botAdapter;
        this.goals = goals;
        this.tip = tip;
    }

    @Override
    public void input(BotParamType inputType, Object value) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void call(BaseBotModel model, String key) {

    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();
        switch (paramType) {
            case TIP_INTRO:
                model.values.put(key, tip.getIntro());
                break;
            default:
                super.resolveParam(model, paramType);
                break;
        }
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case GOALS:
                return goals;
            default:
                return super.getObject(objType);
        }
    }
}
