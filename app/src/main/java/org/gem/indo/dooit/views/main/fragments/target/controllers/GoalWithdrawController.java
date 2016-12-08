package org.gem.indo.dooit.views.main.fragments.target.controllers;

import android.app.Activity;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.controllers.BotCallType;
import org.gem.indo.dooit.controllers.BotParamType;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalTransaction;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/27.
 */

public class GoalWithdrawController extends GoalBotController {

    @Inject
    transient GoalManager goalManager;

    public GoalWithdrawController(Activity activity, Goal goal, Tip tip) {
        super(activity, BotType.GOAL_WITHDRAW, goal, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case DO_WITHDRAW:
                doWithdraw(answerLog);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void input(BotParamType inputType, Object value) {

    }

    private void doWithdraw(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("withdraw_amount")) {
            // Withdraw subtracts from the goal
            GoalTransaction trans = goal.createTransaction(-1 * Double.parseDouble(answerLog.get("withdraw_amount").getValue()));

            goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }).subscribe(new Action1<EmptyResponse>() {
                @Override
                public void call(EmptyResponse emptyResponse) {
                    if (context instanceof MainActivity)
                        ((MainActivity) context).refreshGoals();
                }
            });
        }
    }
}
