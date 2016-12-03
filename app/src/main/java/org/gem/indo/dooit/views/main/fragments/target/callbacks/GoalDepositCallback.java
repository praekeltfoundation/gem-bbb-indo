package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import android.app.Activity;
import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.GoalTransaction;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.BotCallback;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/21.
 */

public class GoalDepositCallback extends BotCallback {

    @Inject
    transient GoalManager goalManager;

    private Context context;
    private Goal goal;

    public GoalDepositCallback(Activity activity, Goal goal) {
        super(activity);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        context = activity;
        this.goal = goal;
    }

    @Override
    public void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case "do_deposit":
                doDeposit(answerLog);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public Object getObject() {
        return goal;
    }

    private void doDeposit(Map<String, Answer> answerLog) {
        GoalTransaction trans = goal.createTransaction(Double.parseDouble(answerLog.get("deposit_amount").getValue()));

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
