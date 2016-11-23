package com.nike.dooit.views.main.fragments.target.callbacks;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.GoalTransaction;
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
        GoalTransaction trans = new GoalTransaction(Double.parseDouble(answerLog.get("depositAmount").getValue()));

        goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe();
    }
}
