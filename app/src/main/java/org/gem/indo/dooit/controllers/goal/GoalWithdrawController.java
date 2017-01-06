package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.TransactionResponse;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
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

    public GoalWithdrawController(Activity activity, BotRunner botRunner, Goal goal, BaseChallenge challenge, Tip tip) {
        super(activity, botRunner, BotType.GOAL_WITHDRAW, goal, challenge, tip);
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
    public void onAnswerInput(BotParamType inputType, Answer answer) {

    }

    @Override
    public boolean filter(Answer answer) {
        switch (answer.getName()) {
            case "goal_withdraw_tip":
                return tip != null;
            case "goal_withdraw_challenge":
                return challenge != null && challenge.isActive();
            default:
                return true;
        }
    }

    private void doWithdraw(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("withdraw_amount")) {
            // Withdraw subtracts from the goal
            GoalTransaction trans = goal.createTransaction(-1 * Double.parseDouble(answerLog.get("withdraw_amount").getValue()));

            goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }).subscribe(new Action1<TransactionResponse>() {
                @Override
                public void call(TransactionResponse response) {
                    if (context instanceof MainActivity)
                        ((MainActivity) context).refreshGoals();
                }
            });
        }
    }
}
