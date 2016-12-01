package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.GoalTransaction;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.BotCallback;

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
        GoalTransaction trans = new GoalTransaction(Double.parseDouble(answerLog.get("deposit_amount").getValue()));

        goalManager.addGoalTransaction(goal, trans, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe();
    }

    @Override
    public void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {

    }
}
