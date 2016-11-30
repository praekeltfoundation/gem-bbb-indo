package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.GoalTransaction;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BotCallback;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/27.
 */

public class GoalWithdrawCallback implements BotCallback {

    @Inject
    transient GoalManager goalManager;

    @Inject
    transient Persisted persisted;

    private Goal goal;

    public GoalWithdrawCallback(DooitApplication application, Goal goal) {
        application.component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("withdraw_amount")) {
            // Withdraw subtracts from the goal
            GoalTransaction trans = new GoalTransaction(-1 * Double.parseDouble(answerLog.get("withdraw_amount").getValue()));

            goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }).subscribe(new Action1<EmptyResponse>() {
                @Override
                public void call(EmptyResponse emptyResponse) {
                    persisted.clearGoals();
                }
            });
        }
    }
}
