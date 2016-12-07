package org.gem.indo.dooit.views.main.fragments.target.controllers;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.controllers.BotParamType;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.bot.BaseBotModel;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class GoalBotController extends DooitBotController {

    @Inject
    protected GoalManager goalManager;

    protected Goal goal;

    public GoalBotController(Context context, Goal goal) {
        super(context);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
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
