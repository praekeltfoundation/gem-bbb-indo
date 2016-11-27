package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BotCallback;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/11/27.
 */

public class GoalWithdrawCallback implements BotCallback {

    @Inject
    transient GoalManager goalManager;

    private Goal goal;

    public GoalWithdrawCallback(DooitApplication application, Goal goal) {
        application.component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }
}
