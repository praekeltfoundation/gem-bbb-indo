package org.gem.indo.dooit.views.main.fragments.target.controllers;

import android.app.Activity;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalTransaction;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/21.
 */

public class GoalDepositController extends GoalBotController {

    @Inject
    transient GoalManager goalManager;

    public GoalDepositController(Activity activity, Goal goal, Tip tip) {
        super(activity, BotType.GOAL_DEPOSIT, goal, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void onAsyncCall(String key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case "do_deposit":
                doDeposit(answerLog, listener);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    private void doDeposit(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        GoalTransaction trans = goal.createTransaction(Double.parseDouble(answerLog.get("deposit_amount").getValue()));

        goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        });

        persisted.saveConvoGoal(botType, goal);
    }
}