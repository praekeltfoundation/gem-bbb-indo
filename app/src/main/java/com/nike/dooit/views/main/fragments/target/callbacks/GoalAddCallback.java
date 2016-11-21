package com.nike.dooit.views.main.fragments.target.callbacks;

import android.util.Log;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BotCallback;

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
                .parseLocalDate(answerLog.get("goalDate").getValue().substring(0, 9)));
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
