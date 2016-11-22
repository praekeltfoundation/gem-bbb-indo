package com.nike.dooit.views.main.fragments.target.callbacks;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BotCallback;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/11/21.
 */

public class GoalDepositCallback implements BotCallback {

    @Inject
    transient GoalManager goalManager;

    private Goal goal;

    public GoalDepositCallback(DooitApplication application, Goal goal) {
        application.component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }
}
