package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.TransactionResponse;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalTransaction;
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
    GoalManager goalManager;

    public GoalDepositController(Activity activity, BotRunner botRunner, Goal goal, BaseChallenge challenge, Tip tip) {
        super(activity, botRunner, BotType.GOAL_DEPOSIT, goal, challenge, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case DO_DEPOSIT:
                doDeposit(answerLog, listener);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public boolean filterQuickAnswer(Answer answer) {
        switch (answer.getName()) {
            case "goal_deposit_tip":
                return tip != null;
            case "goal_deposit_challenge":
                return challenge != null && challenge.isActive();
            default:
                return true;
        }
    }

    private void doDeposit(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        GoalTransaction trans = goal.createTransaction(Double.parseDouble(answerLog.get("deposit_amount").getValue()));

        goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
            }
        }).subscribe(new Action1<TransactionResponse>() {
            @Override
            public void call(final TransactionResponse response) {
                goal.addNewBadges(response.getNewBadges());
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        });

        persisted.saveConvoGoal(botType, goal);
    }
}
