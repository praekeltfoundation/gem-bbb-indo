package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import android.util.Log;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BotCallback;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public class GoalAddCallback implements BotCallback {

    private static final String TAG = GoalAddCallback.class.getName();

    @Inject
    transient GoalManager goalManager;

    public GoalAddCallback(DooitApplication application) {
        application.component.inject(this);
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {
        Goal goal = new Goal();
        goal.setName(answerLog.get("goalName").getValue());
        goal.setTarget(Float.parseFloat(answerLog.get("goalAmount").getValue()));
        goal.setStartDate(LocalDate.now());
        goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                .parseLocalDate(answerLog.get("goalDate").getValue().substring(0, 10)));

        if (answerLog.containsKey("hasSavedY"))
            goal.createTransaction(Double.parseDouble(answerLog.get("priorSaveAmount").getValue()));

        Log.d(TAG, "Created " + goal);

        goalManager.createGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<Goal>() {
            @Override
            public void call(Goal goal) {
                Log.d(TAG, goal + " successfully created");
            }
        });
    }
}
