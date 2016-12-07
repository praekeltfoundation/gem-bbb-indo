package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import android.content.Context;

import org.gem.indo.dooit.controllers.BotParamType;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.BaseBotModel;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class GoalBotController extends DooitBotController {

    protected Goal goal;

    public GoalBotController(Context context, Goal goal) {
        super(context);
        this.goal = goal;
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        switch (paramType) {
            case GOAL_NAME:
                model.values.put(paramType.getKey(), goal.getName());
                break;
            case GOAL_VALUE:
                model.values.put(paramType.getKey(), goal.getValue());
                break;
            case GOAL_TARGET:
                model.values.put(paramType.getKey(), goal.getTarget());
            default:
                super.resolveParam(model, paramType);
        }
    }

    @Override
    public Object getObject() {
        return goal;
    }
}
